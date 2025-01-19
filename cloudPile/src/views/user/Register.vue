<template>
  <div class="register-box">
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

    <el-input class="register-input"
              size="large"
              v-model="user.nick_name"
              placeholder="请输入昵称"
    />
    <el-input class="register-input"
              size="large"
              v-model="user.email"
              placeholder="请输入邮箱"
    />
    <el-input class="register-input"
              size="large"
              v-model="user.password"
              placeholder="请输入密码"
              type="password"

    />
    <el-input class="register-input"
              size="large"
              v-model="user.confirmPassword"
              placeholder="确认密码"
              type="password"

    />
    <el-link style="float: left" @click="router.push({name: 'login'})" type="primary">返回登陆</el-link>

    <el-button class="register-button" size="large" type="primary" @click="handleRegister">注&nbsp;册</el-button>
  </div>
</template>


<script setup>
import {ref} from 'vue';
import {ElMessage} from 'element-plus';
import {isStringEmpty, isValidLength} from "@/utils/tools.js";
import {register} from "@/api/user.js";
import router from "@/router/index.js";
import logo from "@/assets/images/logo.png"

const user = ref({
  nick_name: '',
  email: '',
  password: '',
  confirmPassword: ''
});

const handleRegister = async () => {
  if (isStringEmpty(user.value.nick_name)) {
    ElMessage.error("昵称为未填写");
    return;
  } else if (isStringEmpty(user.value.email)) {
    ElMessage.error("邮箱未填写");
    return;
  } else if (isStringEmpty(user.value.password)) {
    ElMessage.error("密码未填写");
    return;
  } else if (isStringEmpty(user.value.confirmPassword)) {
    ElMessage.error("第二次密码未填写");
    return;
  } else if (!isValidLength(user.value.password)) {
    ElMessage.error("密码长度应在6-22位之间")
    return;
  } else if (user.value.password !== user.value.confirmPassword) {
    ElMessage.error("两次密码不一致")
    return;
  }

  // 发送注册请求
  var response = await register(user.value.nick_name, user.value.email, user.value.password);
  ElMessage.success("注册成功！");
  await router.replace('/user/login');
  window.location.reload(true);
}


</script>


<style scoped>
.register-box {
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

.register-input {
  width: 100%;
  margin-bottom: 10px;
  box-sizing: border-box; /* 确保内边距不影响宽度 */
}

.register-button {
  width: 100%;
  margin-top: 10px;
}

/* 响应式设计：手机屏幕 */
@media (max-width: 768px) {
  .register-input,
  .register-button {
    font-size: 16px; /* 根据需要调整字体大小 */
  }
}


</style>