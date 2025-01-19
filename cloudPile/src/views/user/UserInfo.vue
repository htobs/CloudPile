<template>
  <div class="content">
    <el-image
        style="width: 80px; height: 80px"
        :src="logo"
        :zoom-rate="1.2"
        :max-scale="7"
        :min-scale="0.2"
        :initial-index="4"
        fit="cover"
        class="logo"
    />
    <el-button class="user-button" size="large" type="primary" @click="handleFeedback">问题反馈</el-button>
    <el-button class="user-button" size="large" type="primary" @click="handleLogout">退出登录</el-button>

  </div>
</template>
<script setup>
import {ref} from "vue";
import {CookieManager} from "@/utils/cookies.js";
import logo from "@/assets/images/logo.png"
import {ElMessage, ElMessageBox} from 'element-plus';

/**
 * 退出登录
 */
const handleLogout = () => {
  CookieManager.eraseAllCookies();
  window.location.reload(true);
  ElMessage.success("用户退出登录");
}
/**
 * 问题反馈
 */
const handleFeedback = () => {
  ElMessageBox.alert('如出现问题添加微信：ljg249454 进行反馈', '提示', {
    confirmButtonText: '确定',
  });
}
</script>
<style scoped>
.content {
  display: block;
  margin-top: 180px;
  margin-left: 15px;
  margin-right: 15px;
}

.logo {
  border-radius: 50%;
  margin: 0 auto 15px;
  display: block;
}

.user-button {
  width: 100%;
  margin: 10px;
}
</style>