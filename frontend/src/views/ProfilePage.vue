<template>
  <div class="profile-page">
    <h2>회원정보</h2>
    <p><strong>Email:</strong> {{ user.email }}</p>
    <p><strong>Nickname:</strong> {{ user.nickname }}</p>

    <div class="button-group">
      <button @click="goToChangeNickname">닉네임 변경</button>
      <button @click="goToChangePassword">비밀번호 변경</button>
      <button @click="logout">로그아웃</button>
      <button @click="deleteAccount">회원 탈퇴</button>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { useUserStore } from '../stores/user';
import { useRouter } from 'vue-router';

// Pinia store 사용
const store = useUserStore();
const router = useRouter();

// 사용자 정보 가져오기
const user = store.$state; // Pinia 상태에서 사용자 정보 가져오기

// 페이지 이동 함수들
const goToChangeNickname = () => {
  router.push('/change-nickname');
};

const goToChangePassword = () => {
  router.push('/change-password');
};

// 로그아웃 함수 (백엔드 API 호출)
const logout = async () => {
  try {
    await store.logout(); // store에서 로그아웃 처리 (백엔드 API 호출 포함)
    router.push('/login'); // 로그아웃 후 로그인 페이지로 이동
  } catch (error) {
    alert('로그아웃에 실패했습니다.');
  }
};

// 회원 탈퇴 함수
const deleteAccount = async () => {
  if (confirm('정말로 회원을 탈퇴하시겠습니까?')) {
    try {
      await store.logout(); // 회원 탈퇴 시 로그아웃 처리
      router.push('/signup'); // 회원 탈퇴 후 회원가입 페이지로 이동
    } catch (error) {
      alert('회원 탈퇴에 실패했습니다.');
    }
  }
};

// 페이지 로딩 시 사용자 정보 불러오기
store.fetchUserProfile(); // 컴포넌트 초기화 시 API로 사용자 정보 불러오기
</script>

<style scoped>
.profile-page {
  max-width: 400px;
  margin: 0 auto;
}
.button-group {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: 20px;
}
button {
  padding: 10px;
  font-size: 14px;
}
</style>
