<!-- RuoYi 登录页 -->
<template>
  <div class="login-container flx-center">
    <div class="login-box">
      <SwitchDark class="dark" />
      <div class="login-left">
        <img class="login-left-img" src="@/assets/images/login_left.png" alt="login" />
      </div>
      <div class="login-form">
        <div class="login-logo">
          <h2 class="logo-text">RuoYi Vue</h2>
        </div>
        <el-tabs v-model="activeName" class="demo-tabs">
          <el-tab-pane label="账号登录" name="pwd">
            <LoginForm />
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
  <Footer />
</template>

<script setup lang="ts" name="login">
import LoginForm from "./components/pwd-login/index.vue";
import SwitchDark from "@/components/SwitchDark/index.vue";
import Footer from "@/layouts/components/Footer/index.vue";

const activeName = ref("pwd");
</script>

<style scoped lang="scss">
@use "./index";
.demo-tabs > .el-tabs__content {
  padding: 32px;
  font-size: 32px;
  font-weight: 600;
  color: #6b778c;
}
</style>
