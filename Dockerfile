FROM harbor.local.hching.com/library/jdk:8u301

ADD assembly-package/target/wedatasphere-exchangis-1.1.2.tar.gz /opt/wedatasphere-exchangis.tar.gz

RUN cd /opt/wedatasphere-exchangis.tar.gz/packages/ && tar -zxf exchangis-server_1.1.2.tar.gz && cd /opt/wedatasphere-exchangis.tar.gz/sbin

WORKDIR /opt/wedatasphere-exchangis.tar.gz/sbin

ENTRYPOINT ["/bin/bash start.sh"]
