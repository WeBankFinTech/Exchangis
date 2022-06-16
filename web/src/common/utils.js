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
}

export const dateFormatSeconds = (timestamp) => {
  return moment(timestamp).format("YYYY-MM-DD HH:mm:ss");
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

export const moveUpDown = (targetSelector, wrapSelector, moveTopSelector, boundaryTop = 200, boundaryBottom = 200) => {
  let box = document.querySelector(targetSelector),
    top = moveTopSelector ? document.querySelector(moveTopSelector) : box,
    wrap = document.querySelector(wrapSelector)
  top.onmousedown = (ev) => {
    let e = ev || window.event;
    //计算出鼠标按下的点到box的上侧边缘
    let restTop = e.clientY - box.offsetTop;
    let topRestTop = e.clientY - top.offsetTop;
    document.onmousemove = function (ev) {
      let e = ev || window.event;
      let boxTop = e.clientY - restTop;
      let topTop = e.clientY - topRestTop;
      const contentHeight = document.body.offsetHeight - wrap.offsetTop
      // 头部限制
      if (boxTop - wrap.offsetTop < boundaryTop) {
        boxTop = boundaryTop
      }
      if (topTop - wrap.offsetTop + top.offsetHeight < boundaryTop) {
        topTop = boundaryTop - top.offsetHeight
      }
      // 底部限制
      if (contentHeight - boxTop < boundaryBottom) {
        boxTop = contentHeight - boundaryBottom
      }
      if (contentHeight - topTop -top.offsetHeight < boundaryBottom) {
        topTop = contentHeight - boundaryBottom - top.offsetHeight
      }

      box.style.top = boxTop + "px"
      box.style.height = contentHeight - boxTop + top.offsetHeight + "px"
      top.style.top = topTop + "px"

      // 设置textarea滚动
      let area = document.querySelector('.ant-tabs-tabpane-active.log-textarea')
      if (area) {
        area.style.overflowY = 'auto'
        area.style.height = contentHeight - boxTop - 45 + "px"
      }
    };
    document.onmouseup = function () {
      document.onmousemove = null;
      document.onmouseup = null;
    }
  }
}

export const getEnvironment = () => {
  return localStorage.getItem('exchangis_environment') || ''
}