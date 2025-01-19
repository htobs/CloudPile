import {createRouter, createWebHistory} from 'vue-router'
import {CookieManager} from "@/utils/cookies.js";
import {isJwtExpired} from "@/utils/jwt.js";

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'index',
            component: () => import("../views/index/index.vue"),
        },
        // {
        //     path: '/user',
        //     name: 'user',
        //     children: [
        //         {
        //             path : "login",
        //             name : "userLogin",
        //             component: () => import("../views/user/Login.vue"),
        //         },
        //         {
        //             path : "register",
        //             name : "userRegister",
        //             component: () => import("../views/user/Register.vue"),
        //         }
        //     ]
        // }
    ],
})
/**
 * 路由守卫
 */
router.beforeEach((to, from, next) => {
    // 获取JWT Token
    const token = CookieManager.getCookie('token');

    // 如果路由需要认证，检查Token是否有效
    if (to.matched.some(record => record.meta.requiresAuth)) {
        if (!token) {
            // 没有Token，重定向到登录页面
            next({name: 'login'});
        } else {
            // 有Token，检查是否过期
            if (isJwtExpired(token)) {
                // Token过期，重定向到登录页面
                next({name: 'login'});
            } else {
                // Token有效，继续访问
                next();
            }
        }
    } else {
        // 路由不需要认证，继续访问
        next();
    }
});
export default router
