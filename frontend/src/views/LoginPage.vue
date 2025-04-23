<template>
    <div class="login-container">
      <h2>로그인</h2>
      <form @submit.prevent="login">
        <div class="form-group">
          <label for="email">이메일</label>
          <input v-model="email" type="email" id="email" placeholder="이메일을 입력하세요" required />
        </div>
        <div class="form-group">
          <label for="password">비밀번호</label>
          <input v-model="password" type="password" id="password" placeholder="비밀번호를 입력하세요" required />
        </div>
        <button type="submit" class="login-button">로그인</button>
      </form>
      <div class="link">
        <router-link to="/forgot-password">비밀번호를 잊으셨나요?</router-link>
      </div>
    </div>
  </template>
  
  <script lang="ts" setup>
  import { ref } from 'vue';
  import { useRouter } from 'vue-router';
  import { useUserStore } from '../stores/user';
  
  // Pinia store 사용
  const store = useUserStore();
  const router = useRouter();
  
  // 로컬 상태 변수
  const email = ref('');
  const password = ref('');
  
  // 로그인 함수
  const login = async () => {
    try {
      // store의 login 액션 호출
      await store.login(email.value, password.value);
      router.push('/profile'); // 로그인 후 프로필 페이지로 이동
    } catch (error) {
      alert('로그인에 실패했습니다. 다시 시도해 주세요.');
    }
  };
  </script>
  
  <style scoped>
  .login-container {
    max-width: 400px;
    margin: 50px auto;
    padding: 20px;
    border: 1px solid #ccc;
    border-radius: 8px;
    background-color: #f9f9f9;
  }
  
  h2 {
    text-align: center;
  }
  
  .form-group {
    margin-bottom: 20px;
  }
  
  label {
    display: block;
    margin-bottom: 5px;
  }
  
  input {
    width: 100%;
    padding: 10px;
    font-size: 16px;
    border-radius: 4px;
    border: 1px solid #ccc;
  }
  
  button {
    width: 100%;
    padding: 10px;
    background-color: #4CAF50;
    color: white;
    font-size: 16px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
  }
  
  button:hover {
    background-color: #45a049;
  }
  
  .link {
    text-align: center;
    margin-top: 10px;
  }
  </style>
  