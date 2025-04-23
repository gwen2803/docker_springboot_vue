<template>
    <div class="change-password-container">
      <h2>비밀번호 변경</h2>
      <form @submit.prevent="changePassword">
        <div class="form-group">
          <label for="currentPassword">현재 비밀번호</label>
          <input
            v-model="currentPassword"
            type="password"
            id="currentPassword"
            placeholder="현재 비밀번호를 입력하세요"
            required
          />
        </div>
        <div class="form-group">
          <label for="newPassword">새 비밀번호</label>
          <input
            v-model="newPassword"
            type="password"
            id="newPassword"
            placeholder="새 비밀번호를 입력하세요"
            required
          />
        </div>
        <div class="form-group">
          <label for="confirmPassword">새 비밀번호 확인</label>
          <input
            v-model="confirmPassword"
            type="password"
            id="confirmPassword"
            placeholder="새 비밀번호를 한 번 더 입력하세요"
            required
          />
        </div>
        <button type="submit" class="change-password-button">비밀번호 변경</button>
      </form>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue';
  import { useUserStore } from '../stores/user';
  import { useRouter } from 'vue-router';
  
  const currentPassword = ref('');
  const newPassword = ref('');
  const confirmPassword = ref('');
  const router = useRouter();
  
  const userStore = useUserStore();
  
  // 비밀번호 변경 함수
  const changePassword = async () => {
    if (newPassword.value !== confirmPassword.value) {
      alert('새 비밀번호와 새 비밀번호 확인이 일치하지 않습니다.');
      return;
    }
  
    try {
      await userStore.changePassword(currentPassword.value, newPassword.value);
      alert('비밀번호가 성공적으로 변경되었습니다.');
      router.push('/profile'); // 비밀번호 변경 후 프로필 페이지로 리다이렉트
    } catch (error) {
      alert('비밀번호 변경에 실패했습니다. 다시 시도해주세요.');
    }
  };
  </script>
  
  <style scoped>
  .change-password-container {
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
  </style>
  