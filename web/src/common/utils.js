import { cloneDeep, isNil, isInteger } from "lodash-es";
import moment from "moment";
import { BASE_URL } from "./constants";

export const arrToObj = (arr, key) => {
  if (!Array.isArray(arr)) return null;
  if (!key) {
    console.error("arrToObj: key is required");
    return null;
  }
  return arr.reduce((acc, cur) => {
    acc[cur[key]] = cur;
    return acc;
  }, {});
};

export const extractionObj = (obj, standard) => {
  const cloneObj = cloneDeep(obj);
  const cloneStandard = cloneDeep(standard);
  Object.key(standard).forEach((key) => {
    if (!isNil(cloneObj[key])) {
      cloneStandard[key] = cloneObj[key];
    }
  });
  return cloneStandard;
};

export const isPositiveInteger = (num) => isInteger(num) && num > 0;

export const nonNegativeInteger = (num) => isInteger(num) && num >= 0;

export function getQueryVariable(variable) {
  const query = window.location.search.substring(1);
  const vars = query.split("&");
  for (let i = 0; i < vars.length; i++) {
    const pair = vars[i].split("=");
    if (pair[0] === variable) {
      return pair[1];
    }
  }
  return false;
}

export const genFileURL = (fileId, fileHash) =>
  `${BASE_URL}/record/download/file/${fileId}/${fileHash}`;

export const dateFormat = (timestamp) => {
  return moment(timestamp).format("YYYY-MM-DD HH:mm");
};

/*
 * 随机字符串生成
 */
export const randomString = (len) => {
  len = len || 12;
  let $chars = "ABCDEFGHJKMNPQRSTWXYZabcdefhijkmnprstwxyz";
  let maxPos = $chars.length;
  let pwd = "";
  for (let i = 0; i < len; i++) {
    pwd += $chars.charAt(Math.floor(Math.random() * maxPos));
  }
  return pwd;
};
