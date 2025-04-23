<template>
  <div class="change-nickname-container">
    <h2>닉네임 변경</h2>
    <form @submit.prevent="changeNickname">
      <div class="form-group">
        <label for="nickname">새 닉네임</label>
        <input v-model="newNickname" type="text" id="nickname" placeholder="새 닉네임을 입력하세요" required />
      </div>
      <button type="submit" class="submit-button">변경</button>
    </form>
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue';
import { useUserStore } from '../stores/user';
import { useRouter } from 'vue-router';

// Pinia store 사용
const store = useUserStore();
const router = useRouter();

// 새 닉네임 입력 받기
const newNickname = ref('');

// 닉네임 변경 함수
const changeNickname = async () => {
  try {
    // Pinia store를 통해 API 호출 (닉네임 업데이트)
    await store.updateNickname(newNickname.value);
    alert('닉네임이 성공적으로 변경되었습니다.');
    router.push('/profile'); // 변경 후 프로필 페이지로 이동
  } catch (error) {
    alert('닉네임 변경에 실패했습니다.');
  }
};
</script>

<style scoped>
.change-nickname-container {
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
