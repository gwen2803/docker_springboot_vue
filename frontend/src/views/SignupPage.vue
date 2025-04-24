<template>
  <div class="signup-container">
    <h2>회원가입</h2>
    <form @submit.prevent="signup">
      <div class="form-group">
        <label for="email">이메일</label>
        <input v-model="email" type="email" id="email" placeholder="이메일을 입력하세요" required />
      </div>
      <div class="form-group">
        <label for="password">비밀번호</label>
        <input v-model="password" type="password" id="password" placeholder="비밀번호를 입력하세요" required />
      </div>
      <div class="form-group">
        <label for="nickname">닉네임</label>
        <input v-model="nickname" type="text" id="nickname" placeholder="닉네임을 입력하세요" required />
      </div>
      <button type="submit" class="signup-button">회원가입</button>
    </form>
    <div class="link">
      <router-link to="/login">이미 계정이 있으신가요?</router-link>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '../stores/user';

const email = ref('');
const password = ref('');
const nickname = ref('');

const store = useUserStore();
const router = useRouter();

const signup = async () => {
  try {
    await store.signup(email.value, password.value, nickname.value);
    alert('회원가입이 완료되었습니다!');
    router.push('/login');
  } catch (error) {
    console.error('회원가입 실패:', error);
    alert('회원가입에 실패했습니다.');
  }
};
</script>

<style scoped>
.signup-container {
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
  background-color: #2196F3;
  color: white;
  font-size: 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:hover {
  background-color: #1976D2;
}

.link {
  text-align: center;
  margin-top: 10px;
}
</style>
