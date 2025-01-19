<script setup>
import {RouterLink, RouterView} from 'vue-router'
import {loading} from "@/utils/loading.js";
</script>

<template>
  <div v-loading="loading" element-loading-text="加载中...">
    <el-scrollbar height="100vh">
      <RouterView/>
    </el-scrollbar>
  </div>
</template>
