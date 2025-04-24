import { createRouter, createWebHistory } from 'vue-router';
import type { RouteRecordRaw } from 'vue-router';

import LoginPage from '../views/LoginPage.vue'
import ProfilePage from '../views/ProfilePage.vue'
import ChangeNicknamePage from '../views/ChangeNickname.vue';
import ChangePasswordPage from '../views/ChangePasswordPage.vue';
import ForgotPasswordPage from '../views/ForgotPasswordPage.vue';
import SignupPage from '../views/SignupPage.vue';

const routes: Array<RouteRecordRaw> = [
  { path: '/', redirect: '/login' },  
  { path: '/login', component: LoginPage },
  { path: '/profile', component: ProfilePage },
  { path: '/forgot-password', component: ForgotPasswordPage },
  { path: '/change-nickname', component: ChangeNicknamePage },
  { path: '/change-password', component: ChangePasswordPage },
  { path: '/signup', component: SignupPage },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
