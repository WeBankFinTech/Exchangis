#!/bin/bash/python
# -*- coding:utf-8 -*-
#
# Copyright 2020 WeBank
#
# Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# description:
# EN: UDES Client
# example:
# >> ./exchangis_client.py (-t|--template) (-s|--source) hive (-d|--dest) hive
# >> ./exchangis_client.py (-e|--exec) job.json
# created by davidHua 2019/01
import getopt
import sys
import httplib
import json
import os
import logging
import binascii
import time
from string import Template
import re


from io import BytesIO

reload(sys)
sys.setdefaultencoding('utf8')

OUTPUT_PATH = "./job.json"
TEMPLATE_MODE = 0
EXEC_MODE = 1
ENCRYPT_MODE = 2

UDES_SERVER_HOST = "127.0.0.1"

UDES_TPL_URL_PREFIX = "/api/v1/job/tpl"
UDES_EXEC_URL = "/api/v1/jobinfo/run"
UDES_PWD_ENCRYPT_URL = "/pwd/encrypt"
UDES_JOB_STATUS_URL = "/api/v1/jobtask/status/list"
UDES_REQ_TIMEOUT = 60
UDES_CHECK_INTERVAL = 5

ENV = os.environ
logging.getLogger().setLevel(logging.INFO)

# to set auth information in environment
ENV["UDES_AUTH_ID"] = "davidhua"
ENV["UDES_AUTH_PWD"] = "cIWPl3ml716xWye94dFRFwpkoImwrWxoOxK0mA/LcA0="


def usage():
    print("-h|-help, show the usage")
    print("-t|--template, template mode,  you should use (-s|--source) and (-d|--dest) "
          "to choose the type of  source and dest datasource"
          " run in template mode, will build a template file of job's configuration named 'job.json' default")
    print("\t-s|--source, choose the type of source datasource")
    print("\t-d|--dest, choose the type of dest datasource")
    print("\t-o|--output, the path to output in template mode, default: '$(pwd)/job.json'")
    print("-e|--exec, exec mode, you should use (--job-id) or (--job-conf) after that")
    print("\t--job-id, the id of job defined in front page")
    print("\t--job-conf, the job's configuration file(or content string)")
    print("\t--async, means that executing job asynchronously")
    print("-p|--params, parameter used in job config, eg: -p \"key1=value1,key2=value2\"")
    print("-E, environment variable, eg: -E \"UDES_AUTH_ID=xxx,UDES_AUTH_PWD=xxxx\"")
    print("--encrypt, encrypt user's password, you should append you plaintext after that")
    # print("\t-k|--key, choose the secretKey file")


def parse_to_params(params_str):
    params_output = {}
    pattern = r'^[\s\S]+?=[\s\S]+?(?=,)|(?<=,)[\s\S]+?=[\s\S]+?(?=,[^,]+?=[^,]+)|(?<=,)[\s\S]+?=[\s\S]+$'
    param_str_array = re.findall(pattern, params_str)
    for param_str in param_str_array:
        key_value = param_str.split("=", 1)
        params_output[str(key_value[0]).strip()] = str(key_value[1]).strip()
    return params_output


def set_env(env_str):
    env_params = parse_to_params(env_str)
    for key, value in env_params.items():
        ENV[key] = value


def build(src, dest, output):
    # request the server to get template
    try:
        logging.info("Fetching UDES job template, source_type:" + src + ", dest_type:" + dest)
        client = UDESClient()
        client.request("GET", UDES_TPL_URL_PREFIX + "/src/" + src + "/dest/" + dest)
        resp = client.resp()
        if resp.get_code() != 0:
            raise Exception("Request server failed, code: " + bytes(resp.get_code()) + ", message:" + resp.get_msg())
        else:
            template = resp.get_data_str()
            try:
                fs = open(output, 'w')
                fs.write(template)
            except IOError, he:
                raise Exception("Error occurred when output template file, output:" + output + ", message: " + repr(he))
        client.close()
    except httplib.HTTPException, he:
        raise Exception(
            "Error occurred when getting template from server:" + UDES_SERVER_HOST + ", message: " + repr(he))
    logging.info("Success fetching UDES job template, output:" + output)


def execute_with_job_id(_job_id, _async):
    logging.info("Request to run job whose id : %s" % _job_id)
    try:
        client = UDESClient()
        client.request("GET", UDES_EXEC_URL + "/" + _job_id + "?userName=" + ENV["UDES_AUTH_ID"])
        resp = client.resp()
        if resp.get_code() != 0:
            raise Exception("Request server failed ,code: " + bytes(resp.get_code()) + ", message:" + resp.get_msg())
        client.close()
        if async is False:
            _wait_for_complete(resp.get_data(), UDES_CHECK_INTERVAL)
    except httplib.HTTPConnection, he:
        raise Exception(
            "Error occurred when requesting to execute UDES job to server: " + UDES_SERVER_HOST + ", message: " + repr(he))
    logging.info("Success executing UDES job")


def execute_with_path(path, _job_params, _async):
    logging.info('Uploading and executing UDES job file to server, job_path: %s' % path)
    f = None
    try:
        f = open(path, 'r')
        job_content = f.read()
    finally:
        if f is not None:
            f.close()
    if job_content is not None:
        execute(job_content, _job_params, _async)
    else:
        raise Exception("Cannot read the UDES job file: %s" % path)


def execute(job_content, _job_params, _async):
    try:
        print(job_content)
        job_content = Template(job_content).safe_substitute(**_job_params)
        client = UDESClient()
        # to parse job content
        logging.info('Start to parse job content..')
        job = _parse(job_content)
        files = {}
        auth_file = _get_auth_file(job["config"]["dataSrcParams"])
        if auth_file is not None:
            files["srcAuthFile"] = _file_props("path", auth_file)
        auth_file = _get_auth_file(job["config"]["dataDstParams"])
        if auth_file is not None:
            files["dstAuthFile"] = _file_props("path", auth_file)
        files["job"] = _file_props("string", job_content)
        logging.info("Finish to parse job content, uploading files: " + str(files.keys()))
        client.upload_file("POST", UDES_EXEC_URL, files)
        resp = client.resp()
        if resp.get_code() != 0:
            raise Exception("Request server failed, code: " + bytes(resp.get_code()) + ", message:" + resp.get_msg())
        client.close()
        if _async is False:
            _wait_for_complete(resp.get_data(), UDES_CHECK_INTERVAL)
    except httplib.HTTPConnection, he:
        raise Exception(
            "Error occurred when requesting to execute UDES job to server: " + UDES_SERVER_HOST + ", message: " + repr(he))
    logging.info("Success executing UDES job")


def encrypt(plaintext, key_path):
    try:
        client = UDESClient()
        body = {
            "src_pwd": plaintext
        }
        client.request_with_body("POST", UDES_PWD_ENCRYPT_URL, json.dumps(body), {"Content-Type": "application/json"})
        resp = client.resp()
        logging.info("Success to encrypt UDES password , result: " + resp.get_row_data())
        client.close()
    except httplib.HTTPConnection, he:
        raise Exception("Error occurred when encrypting password, message: " + repr(he))


def _file_props(_type, _content):
    return {
        "type": _type,
        "content": _content
    }


def _wait_for_complete(job_id, check_interval):
    try:
        t0 = time.time()
        error_count = 0
        while True:
            client = UDESClient()
            body = {
                "idList": [job_id]
            }
            client.request_with_body("PUT", UDES_JOB_STATUS_URL, json.dumps(body),
                                     {"SC_AUTH_ID": ENV["UDES_AUTH_ID"], "SC_AUTH_PWD": ENV["UDES_AUTH_PWD"],
                                      "Content-Type": "application/json"})
            resp = None
            try:
                resp = client.resp()
                if resp.get_code() != 0:
                    raise Exception(
                        "Request server failed, code: " + bytes(resp.get_code()) + ", message:" + resp.get_msg())
                status = resp.get_data()[0]
                if status == "SUCCESS" or status == "FAILD" or status == "KILL":
                    logging.info("Job: " + str(job_id) + " run complete, status:" + status)
                    if status == "FAILD" or status == "KILL":
                        exit(1)
                    else:
                        break
            except Exception, e:
                if resp is None and error_count < 3:
                    error_count += 1
                    continue
                else:
                    raise e
            error_count = 0
            logging.info(
                "Checking job: " + str(job_id) + "'s status, status: " + status + ", wait_time: " + str(
                    time.time() - t0))
            client.close()
            time.sleep(check_interval)
    except httplib.HTTPConnection, he:
        raise Exception("Error occurred when getting status of job: " + str(job_id) + ", message:" + repr(he))


def _get_auth_file(_config):
    if "haveKerberos" in _config \
            and _config["haveKerberos"] == "true":
        if "kerberosKeytabFilePath" not in _config:
            raise Exception("cannot find prop 'kerberosKeytabFilePath'")
        return str(_config["kerberosKeytabFilePath"])
    elif "authCreden" in _config and os.path.exists(str(_config["authCreden"])):
        return str(_config["authCreden"])
    return None


def _parse(_job):
    job = json.loads(_job)
    if "config" not in job:
        raise Exception("Illegal content of job configuration , cannot find prop 'config'")
    config = job["config"]
    if "dataSrcParams" not in config:
        raise Exception("Illegal content of job configuration , cannot find prop 'config.dataSrcParams'")
    if "dataDstParams" not in config:
        raise Exception("Illegal content of job configuration , cannot find prop 'config.dataDstParams'")
    return job


class UDESClient:
    def __init__(self, timeout=UDES_REQ_TIMEOUT):
        self._connect = httplib.HTTPConnection(UDES_SERVER_HOST, timeout=timeout)

    def request(self, method, url, headers=None):
        self.request_with_body(method, url, None, headers)

    def request_with_body(self, method, url, body, headers=None):
        try:
            if headers is None:
                headers = {"SC_AUTH_ID": ENV["UDES_AUTH_ID"], "SC_AUTH_PWD": ENV["UDES_AUTH_PWD"]}
            self._connect.request(method=method, url=url,
                                  headers=headers,
                                  body=body)
        except KeyError, ke:
            raise Exception(str(ke) + " should be defined in environment")

    def upload_file(self, method, url, files=None):
        if files is None:
            files = {}
        boundary = binascii.hexlify(os.urandom(16))
        body = BytesIO()
        for name, props in files.items():
            body.write(b'--%s\r\n' % boundary)
            if props["type"] == "path":
                body.write(b'Content-Disposition:form-data;name="%s";filename="%s"\r\n' % (name, props["content"]))
                body.write(b'Content-Type:application/octet-stream\r\n\r\n')
                f = None
                try:
                    f = open(props["content"], 'rb')
                    body.write(f.read())
                    body.write(b'\r\n')
                finally:
                    if f is not None:
                        f.close()
            else:
                body.write(b'Content-Disposition:form-data;name="%s";filename="job.json"\r\n' % name)
                body.write(b'Content-Type:application/octet-stream\r\n\r\n')
                body.write(props["content"])
                body.write(b'\r\n')
        body.write(b'--%s--\r\n' % boundary)
        try:
            self._connect.request(method=method, url=url, body=body.getvalue(),
                                  headers={
                                      "SC_AUTH_ID": ENV["UDES_AUTH_ID"],
                                      "SC_AUTH_PWD": ENV["UDES_AUTH_PWD"],
                                      "Content-Type": "multipart/form-data;boundary=" + boundary,
                                      "Connection": "Keep-Alive"
                                  })
        except KeyError, ke:
            raise Exception(str(ke) + " should be defined in environment")

    def resp(self):
        resp = self._connect.getresponse()
        if resp.status == httplib.OK:
            body = resp.read()
            _resp = UDESResp(body)
            resp.close()
            return _resp
        else:
            raise Exception("Server Response status: " + str(resp.status), "please check ")

    def close(self):
        self._connect.close()


class UDESResp:
    def __init__(self, body):
        self._body = body
        try:
            self._body_json = json.loads(body, encoding="utf-8")
        except Exception, e:
            print('')

    def get_code(self):
        return self._body_json["code"]

    def get_msg(self):
        if "message" in self._body_json:
            return self._body_json["message"]
        else:
            return ""

    def get_data(self):
        return self._body_json["data"]

    def get_row_data(self):
        return self._body

    def get_body(self):
        return self._body_json

    def get_data_str(self):
        return json.dumps(self._body_json["data"], indent=4, sort_keys=True)


if __name__ == "__main__":
    # parse command
    opts = []
    src_type = ""
    dest_type = ""
    mode = -1
    output = OUTPUT_PATH
    job_id = ""
    job_conf = ""
    job_params = {}
    plain_text = ""
    secret_key_path = ""
    async = False
    try:
        opts, args = getopt.getopt(sys.argv[1:], "htes:d:o:p:E:",
                                   ["help", "template", "source=", "output=", "dest=", "exec", "encrypt=",
                                    "async", "job-id=", "job-conf=", "params="])
    except getopt.GetoptError, e:
        logging.error("Invalid command call, please check your command parameters! msg: %s" % str(e))
        exit(1)
    if ("-h", "") in opts or ("--help", "") in opts:
        usage()
        exit(0)
    for arg, v in opts:
        if arg == "-t" or arg == "--template":
            mode = TEMPLATE_MODE
        elif arg == "-e" or arg == "--exec":
            mode = EXEC_MODE
            job_path = v
        elif arg == "-s" or arg == "--source":
            src_type = v
        elif arg == "-d" or arg == "--dest":
            dest_type = v
        elif arg == "--encrypt":
            mode = ENCRYPT_MODE
            plain_text = v
        elif arg == "-k" or arg == "--key":
            secret_key_path = v
        elif arg == "-o" or arg == "--output":
            output = v
        elif arg == "--async":
            async = True
        elif arg == "--job-id":
            job_id = v
        elif arg == "--job-conf":
            job_conf = v
        elif arg == "-p" or arg == "--params":
            job_params = parse_to_params(v)
        elif arg == "-E":
            set_env(v)

    try:
        if mode <= -1:
            logging.error("Please set the operation: -t (--template) or -e (--exec)")
            exit(1)
        elif mode == TEMPLATE_MODE:
            if src_type == "":
                logging.error("Please set source type: -s (--source)")
                exit(1)
            if dest_type == "":
                logging.error("Please set dest type: -d (--dest)")
                exit(1)
            build(src_type, dest_type, output)
        elif mode == EXEC_MODE:
            if job_id == "" and job_conf == "":
                logging.error("Please set job id (--job-id) or job conf (--job-conf)")
            if job_id != "":
                execute_with_job_id(job_id, async)
            else:
                job_conf = job_conf.strip()
                if job_conf.startswith("{") and job_conf.endswith("}"):
                    execute(job_conf, job_params, async)
                else:
                    if os.path.exists(job_conf):
                        execute_with_path(job_conf, job_params, async)
                    else:
                        raise Exception("Invalid job_conf value, please check your command")

        elif mode == ENCRYPT_MODE:
            # if secret_key_path == "":
            #     logging.error("Please set key path: -k (--key)")
            #     exit(1)
            encrypt(plain_text, secret_key_path)
    except Exception, e:
        message = e.message
        if message is None or message == "":
            message = repr(e)
        logging.error("Script run failed: " + message)
        exit(1)
    exit(0)
