<!-- RuoYi 密码登录 -->
<template>
  <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" size="large">
    <el-form-item prop="username">
      <el-input v-model="loginForm.username" placeholder="请输入用户名">
        <template #prefix>
          <el-icon class="el-input__icon">
            <user />
          </el-icon>
        </template>
      </el-input>
    </el-form-item>
    <el-form-item prop="password">
      <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" show-password autocomplete="new-password">
        <template #prefix>
          <el-icon class="el-input__icon">
            <lock />
          </el-icon>
        </template>
      </el-input>
    </el-form-item>
    <el-form-item v-if="captchaEnabled" prop="code">
      <div class="flex-y-center w-full">
        <el-input v-model="loginForm.code" placeholder="请输入验证码">
          <template #prefix>
            <el-icon class="el-input__icon">
              <key />
            </el-icon>
          </template>
        </el-input>
        <div class="w-18px"></div>
        <img :src="captchaImg" @click="getCaptcha" class="cursor-pointer" style="height: 40px" />
      </div>
    </el-form-item>
  </el-form>
  <div class="login-btn">
    <el-button :icon="CircleClose" round size="large" @click="resetForm(loginFormRef)"> 重置 </el-button>
    <el-button :icon="UserFilled" round size="large" type="primary" :loading="auth.loginLoading" @click="handleSubmit(loginFormRef)">
      登录
    </el-button>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onBeforeUnmount } from "vue";
import { Login } from "@/api/interface";
import { loginApi } from "@/api";
import { useAuthStore } from "@/stores/modules";
import { CircleClose, UserFilled } from "@element-plus/icons-vue";
import type { ElForm } from "element-plus";
import { required } from "@/utils/formRules";

const auth = useAuthStore();
const { loginPwd } = useAuthStore();

type FormInstance = InstanceType<typeof ElForm>;
const captchaEnabled = ref(true); // 是否开启验证码
const captchaImg = ref(""); // 验证码图片

const loginFormRef = ref<FormInstance>(); // 表单实例

// RuoYi 登录表单数据
const loginForm = reactive<Login.LoginForm>({
  username: "admin", // 用户名
  password: "admin123", // 密码
  code: "", // 验证码
  uuid: "" // 验证码UUID
});

// 表单验证规则
const loginRules = reactive({
  username: [required("请输入用户名")],
  password: [required("请输入密码")],
  code: [required("请输入验证码")]
});

const handleSubmit = (formEl: FormInstance | undefined) => {
  if (!formEl) return;
  // 表单验证
  formEl.validate(async valid => {
    if (!valid) return;
    try {
      // RuoYi 登录不需要加密密码
      await loginPwd(loginForm);
    } catch (error) {
      // 登录失败刷新验证码
      await getCaptcha();
    }
  });
};

// 重置表单
const resetForm = (formEl: FormInstance | undefined) => {
  if (!formEl) return;
  formEl.resetFields();
};

/** 获取验证码 - RuoYi: GET /captchaImage */
async function getCaptcha() {
  try {
    const res = await loginApi.getCaptcha();
    if (res.code === 200) {
      captchaEnabled.value = res.captchaEnabled !== false;
      if (captchaEnabled.value && res.img) {
        captchaImg.value = "data:image/gif;base64," + res.img;
        loginForm.uuid = res.uuid || "";
      }
    }
  } catch (error) {
    console.error("获取验证码失败", error);
  }
}

onMounted(() => {
  // 监听 enter 事件（调用登录）
  document.onkeydown = (e: KeyboardEvent) => {
    e = (window.event as KeyboardEvent) || e;
    if (e.code === "Enter" || e.code === "enter" || e.code === "NumpadEnter") {
      if (auth.loginLoading) return;
      handleSubmit(loginFormRef.value);
    }
  };
  // 获取验证码
  getCaptcha();
});

onBeforeUnmount(() => {
  document.onkeydown = null;
});
</script>

<style scoped lang="scss">
@use "../../index";
</style>
