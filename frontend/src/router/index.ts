import { createRouter, createWebHistory } from 'vue-router';
import type { RouteRecordRaw } from 'vue-router';

import LoginPage from '../views/LoginPage.vue'
import ProfilePage from '../views/ProfilePage.vue'
import ChangeNicknamePage from '../views/ChangeNickname.vue';

const routes: Array<RouteRecordRaw> = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: LoginPage },
  { path: '/profile', component: ProfilePage },
  { path: '/forgot-password', component: () => import('../pages/ForgotPasswordPage.vue') },
  { path: '/reset-password/:token', component: () => import('../pages/ResetPasswordPage.vue') },
  { path: '/profile', component: () => import('../pages/ProfilePage.vue') },
  { path: '/change-nickname', component: () => import('../pages/ChangeNicknamePage.vue') },
  { path: '/change-password', component: () => import('../pages/ChangePasswordPage.vue') },
  { path: '/delete-account', component: () => import('../pages/DeleteAccountPage.vue') },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
