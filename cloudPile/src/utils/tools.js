/**
 * 检查字符串是否为空
 * @param str 需要检查的字符串
 * @returns {boolean}
 */
export function isStringEmpty(str) {
    return str === null || str.length <= 0 || str.trim() === '';
}

/**
 * 检查字符串长度是否在 6-22之间
 * @param str 需要检查的字符串
 * @returns {boolean}
 */
export function isValidLength(str) {
    return str.length >= 6 && str.length <= 22;
}
