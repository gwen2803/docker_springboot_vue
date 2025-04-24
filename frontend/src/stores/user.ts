import { defineStore } from 'pinia';
import axios from 'axios';

interface UserState {
  email: string;
  nickname: string;
  accessToken: string;
  refreshToken: string;
}

export const useUserStore = defineStore('user', {
  state: (): UserState => ({
    email: '',
    nickname: '',
    accessToken: localStorage.getItem('access_token') || '',
    refreshToken: localStorage.getItem('refresh_token') || '',
  }),
  actions: {
    // 로그인 처리
    async login(email: string, password: string) {
      try {
        const response = await axios.post('/api/auth/login', {
          email: email,
          password: password
        });
        this.setUserInfo(response.data.json.user);
        this.setToken(response.data.json.token.accessToken, response.data.json.token.refreshToken);
        return response.data; // 성공 시 응답 데이터 반환
      } catch (error) {
        console.error('로그인 실패:', error);
        throw new Error('로그인에 실패했습니다.');
      }
    },

    // 닉네임 업데이트
    async updateNickname(newNickname: string) {
      try {
        const response = await axios.post('/api/users/change-nickname', {
          newNickname: newNickname
        }, {
          headers: {
            Authorization: `Bearer ${this.accessToken}`,
          },
        });
        this.nickname = newNickname; // 업데이트된 닉네임을 상태에 반영
        return response.data; // 성공 시 응답 데이터 반환
      } catch (error) {
        console.error('닉네임 변경 실패:', error);
        throw new Error('닉네임 변경에 실패했습니다.');
      }
    },

    // 비밀번호 변경 처리
    async changePassword(currentPassword: string, newPassword: string) {
      try {
        const response = await axios.post('/api/users/change-password', {
          currentPassword: currentPassword,
          newPassword: newPassword
        }, {
          headers: {
            Authorization: `Bearer ${this.accessToken}`,
          },
        }
        );
        return response.data; // 성공 시 응답 데이터 반환
      } catch (error) {
        console.error('비밀번호 변경 실패:', error);
        throw new Error('비밀번호 변경에 실패했습니다.');
      }
    },

    // 비밀번호 찾기 메일 발송
    async sendPasswordResetLink(email: string) {
      try {
        const response = await axios.post('/api/auth/forgot-password', {
          email: email
        });
        return response.data; // 성공 시 응답 데이터 반환
      } catch (error) {
        console.error('비밀번호 재설정 링크 발송 실패:', error);
        throw new Error('비밀번호 재설정 링크 발송에 실패했습니다.');
      }
    },

    async signup(email: string, password: string, nickname: string) {
      try {
        const response = await axios.post('/api/auth/signup', {
          email: email,
          password: password,
          nickname: nickname,
        });
        return response.data; // 성공 시 응답 데이터 반환
      } catch (error) {
        console.error('회원가입 실패:', error);
        throw new Error('회원가입에 실패했습니다.');
      }
    },

    // 토큰 갱신
    async refreshAccessToken() {
      try {
        const response = await axios.post('/api/auth/refresh-token', null, {
          headers: {
            Authorization: `Bearer ${this.refreshToken}`,
          },
        });
        this.setToken(response.data.json.token.accessToken, response.data.json.token.refreshToken);
        return response.data;
      } catch (error) {
        console.error('토큰 갱신 실패:', error);
        throw new Error('토큰 갱신에 실패했습니다.');
      }
    },

    // 사용자 정보 설정
    setUserInfo(user: { email: string; nickname: string }) {
      this.email = user.email;
      this.nickname = user.nickname;
    },

    // 토큰 저장
    setToken(accessToken: string, refreshToken: string) {
      this.accessToken = accessToken;
      this.refreshToken = refreshToken;
      localStorage.setItem('access_token', accessToken);
      localStorage.setItem('refresh_token', refreshToken);
    },

    // 로그아웃 처리 (API 호출 후 상태 초기화)
    async logout() {
      try {
        // 백엔드에서 로그아웃 API 호출
        await axios.post('/api/auth/logout', null, {
          headers: {
            Authorization: `Bearer ${this.accessToken}`,
          },
        });
        // 로그아웃 후 Pinia 상태 초기화 및 로컬스토리지 삭제
        this.accessToken = '';
        this.refreshToken = '';
        this.email = '';
        this.nickname = '';
        localStorage.removeItem('access_token');
        localStorage.removeItem('refresh_token');
        return true;
      } catch (error) {
        console.error('로그아웃 실패:', error);
        throw new Error('로그아웃에 실패했습니다.');
      }
    },

    // 사용자 프로필 정보 불러오기
    async fetchUserProfile() {
      try {
        const response = await axios.get('/api/users/user-info', {
          headers: {
            Authorization: `Bearer ${this.accessToken}`,
          },
        });
        this.setUserInfo(response.data.json.user);
        return response.data;
      } catch (error) {
        console.error('프로필 불러오기 실패:', error);
        throw new Error('프로필 정보를 불러오는 데 실패했습니다.');
      }
    },

    async deleteAccount() {
      try {
        await axios.delete('/api/users', {
          headers: {
            Authorization: `Bearer ${this.accessToken}`,
          },
        });
        // 상태 초기화 및 로컬스토리지 정리
        this.accessToken = '';
        this.refreshToken = '';
        this.email = '';
        this.nickname = '';
        localStorage.removeItem('access_token');
        localStorage.removeItem('refresh_token');
        return true;
      } catch (error) {
        console.error('회원 탈퇴 실패:', error);
        throw new Error('회원 탈퇴에 실패했습니다.');
      }
    }
  },
});
