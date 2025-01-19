/**
 * // 设置cookie
 * CookieManager.setCookie('username', 'John Doe', 7); // 设置一个有效期为7天的cookie
 *
 * // 获取cookie
 * let username = CookieManager.getCookie('username'); // 获取名为'username'的cookie值
 *
 * // 删除cookie
 * CookieManager.eraseCookie('username'); // 删除名为'username'的cookie
 */


/**
 * cookie管理
 */
export  class CookieManager {
    /**
     * 设置cookie
     * @param {string} name - Cookie的名称
     * @param {string} value - Cookie的值
     * @param {number} days - Cookie的有效期（天数）
     */
    static setCookie(name, value, days) {
        let expires = "";
        if (days) {
            let date = new Date();
            date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
            expires = "; expires=" + date.toUTCString();
        }
        document.cookie = name + "=" + (value || "") + expires + "; path=/";
    }

    /**
     * 获取cookie
     * @param {string} name - Cookie的名称
     * @returns {string|null} Cookie的值，如果没有找到则返回null
     */
    static getCookie(name) {
        let nameEQ = name + "=";
        let ca = document.cookie.split(';');
        for (let i = 0; i < ca.length; i++) {
            let c = ca[i];
            while (c.charAt(0) === ' ') c = c.substring(1, c.length);
            if (c.indexOf(nameEQ) === 0) return c.substring(nameEQ.length, c.length);
        }
        return null;
    }

    /**
     * 删除cookie
     * @param {string} name - Cookie的名称
     */
    static eraseCookie(name) {
        this.setCookie(name, "", -1);
    }

    /**
     * 删除所有cookie
     */
    static eraseAllCookies() {
        let cookies = document.cookie.split(";");
        for (let i = 0; i < cookies.length; i++) {
            let cookie = cookies[i].trim();
            let eqPos = cookie.indexOf("=");
            let name = eqPos > -1 ? cookie.substr(0, eqPos) : cookie;
            CookieManager.eraseCookie(name);
        }
    }
}

