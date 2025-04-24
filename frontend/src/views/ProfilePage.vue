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

const store = useUserStore();
const router = useRouter();

const user = store.$state;

const goToChangeNickname = () => {
  router.push('/change-nickname');
};

const goToChangePassword = () => {
  router.push('/change-password');
};

const logout = async () => {
  try {
    await store.logout();
    router.push('/login');
  } catch (error) {
    console.error('로그아웃 실패:', error);
    alert('로그아웃에 실패했습니다.');
  }
};

const deleteAccount = async () => {
  if (confirm('정말로 회원을 탈퇴하시겠습니까?')) {
    try {
      await store.deleteAccount(); // ✅ 여기서 직접 deleteAccount 호출
      router.push('/signup'); // 탈퇴 후 회원가입 페이지로 이동
    } catch (error) {
      console.error('회원 탈퇴 실패:', error);
      alert('회원 탈퇴에 실패했습니다.');
    }
  }
};

// 프로필 정보 불러오기
store.fetchUserProfile();
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
