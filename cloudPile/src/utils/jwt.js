/**
 * 对jwt对象进行解析，包含载荷时间
 * @param jwt
 * @returns {any|null}
 */
export function parseJwtPayload(jwt) {
    if (!jwt) {
        return null;
    }

    const base64Url = jwt.split('.')[1]; // 获取载荷部分
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/'); // 将Base64 URL-safe转换为标准Base64

    const jsonPayload = decodeURIComponent(atob(base64).split('').map(c => {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));

    return JSON.parse(jsonPayload);
}

/**
 * 检查JWT对象是否过期
 * @param jwt
 * @returns {boolean} true过期，false未过期
 */
export function isJwtExpired(jwt) {
    const payload = parseJwtPayload(jwt);
    if (!payload) {
        return true; // 如果解析失败，认为JWT无效或过期
    }

    const exp = payload.exp; // 获取过期时间
    if (exp) {
        // 检查JWT是否过期，exp的时间戳需要转换为毫秒
        return exp * 1000 < Date.now();
    }

    return false; // 如果没有exp字段，认为JWT没有过期
}

/**
 * 解析JWT对象，不包含载荷时间
 * @param jwt
 * @returns {{}|any}
 */
export function decodeJwt(jwt) {
    const payload = parseJwtPayload(jwt);
    if (!payload || isJwtExpired(jwt)) {
        return {};
    }
    return payload["claims"];
}

// const jwt1 = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjbGFpbXMiOnsid2VjaGF0SWQiOiIxMjNhMWMyMyIsImlkIjoxLCJ1c2VybmFtZSI6Iua1i-ivlei0puaItyJ9LCJleHAiOjE3MjgzMDU0NjF9.6ECAYMC1NpR_BoUWBnNMvftNiRi71fhqe5OzhBwj8yI';
// const jwt2 = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjbGFpbXMiOnsidXBkYXRlZF9hdCI6bnVsbCwiYXZhdGFyVXJsIjpudWxsLCJuaWNrX25hbWUiOiI1MDEyM0BxcS5jb20iLCJjcmVhdGVkX2F0IjpudWxsLCJsYW5ndWFnZSI6bnVsbCwiaWQiOjMsImVtYWlsIjoiNTAxMjNAcXEuY29tIiwic3RhdHVzIjpudWxsfSwiZXhwIjoxNzI5NjA0MTYxfQ.XmJAQYOWxyACHAeWD00tcDAMOO7tL5FGpdWcvwbbzjc"
// const decodedPayload = decodeJwt(jwt1);
// console.log(decodedPayload);
//
//
// console.log("检查是否过期: " + isJwtExpired(jwt1));
// console.log("检查是否过期: " + isJwtExpired(jwt2));
// if (isJwtExpired(jwt1)) {
//     console.log('JWT is expired');
// } else {
//     console.log('JWT is valid');
// }