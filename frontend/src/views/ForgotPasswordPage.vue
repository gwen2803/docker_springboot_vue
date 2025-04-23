<template>
    <div class="forgot-password-container">
      <h2>비밀번호 찾기</h2>
      <form @submit.prevent="sendResetLink">
        <div class="form-group">
          <label for="email">이메일 주소</label>
          <input
            v-model="email"
            type="email"
            id="email"
            placeholder="이메일을 입력하세요"
            required
          />
        </div>
        <button type="submit" class="send-reset-link-button">비밀번호 재설정 링크 보내기</button>
      </form>
      <p v-if="message" class="message">{{ message }}</p>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue';
  import { useUserStore } from '../stores/user';
  
  const email = ref('');
  const message = ref('');
  
  // 비밀번호 찾기 요청을 보내는 함수
  const sendResetLink = async () => {
    try {
      // 백엔드에 이메일로 비밀번호 재설정 링크 보내기 요청
      await useUserStore().sendPasswordResetLink(email.value);
      message.value = '비밀번호 재설정 링크가 이메일로 발송되었습니다. 이메일을 확인하세요.';
    } catch (error) {
      message.value = '비밀번호 재설정 링크 발송에 실패했습니다. 다시 시도해주세요.';
    }
  };
  </script>
  
  <style scoped>
  .forgot-password-container {
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
  
  .message {
    margin-top: 20px;
    text-align: center;
    color: green;
  }
  </style>
  