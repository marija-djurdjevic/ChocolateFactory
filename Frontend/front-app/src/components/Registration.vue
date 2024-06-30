<template>
    <div class="register-container">
      <header class="status-bar">
        <h1>Register</h1>
      </header>
      <div class="register-form">
        <h2>Create an Account</h2>
        <form @submit.prevent="register">
          <div class="form-group">
            <label for="username">Username</label>
            <input type="text" id="username" v-model="user.username" required>
          </div>
          <div class="form-group">
            <label for="password">Password</label>
            <input type="password" id="password" v-model="user.password" required>
          </div>
          <div class="form-group">
            <label for="confirmPassword">Confirm Password</label>
            <input type="password" id="confirmPassword" v-model="confirmPassword" required>
          </div>
          <div class="form-group">
            <label for="name">First Name</label>
            <input type="text" id="name" v-model="user.name" required>
          </div>
          <div class="form-group">
            <label for="surname">Last Name</label>
            <input type="text" id="surname" v-model="user.surname" required>
          </div>
          <div class="form-group">
            <label for="gender">Gender</label>
            <select id="gender" v-model="user.gender" required>
              <option value="male">Male</option>
              <option value="female">Female</option>
            </select>
          </div>
          <div class="form-group">
            <label for="birthDate">Date of Birth</label>
            <input type="date" id="birthDate" v-model="user.birthDate" required>
          </div>
          <div class="form-group">
            <button type="submit">Register</button>
          </div>
        </form>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue';
  import { useRouter } from 'vue-router';
  import axios from 'axios';

  const confirmPassword = ref('');

  const user = ref({
    username: '',
    password: '',
    name: '',
    surname: '',
    gender: '',
    birthDate: ''
  });
  
  const router = useRouter();
  
  function register() {
    if (user.value.password !== confirmPassword.value) {
      alert('Passwords do not match!');
      return;
    }

    axios.post('http://localhost:8080/WebShopAppREST/rest/users/saveCustomer', user.value)
      .then(response => {
        alert('User added successfully!');
        router.push('/');
      })
      .catch(error => {
        console.error('Error adding user:', error);
      });

  }
  </script>
  
  <style>
  .register-container {
    text-align: center;
    padding: 20px;
    min-height: 100vh;
  }
  
  .status-bar {
    background-color: #FFC9AD; /* Bisque */
    color: #333;
    padding: 10px;
    margin-bottom: 20px;
  }
  
  .status-bar h1 {
    font-size: 2rem;
  }
  
  .register-form {
    background-color: #ffe4b5; /* Bisque */
    border-radius: 10px;
    padding: 20px;
    max-width: 400px;
    margin: 0 auto;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  }
  
  .register-form h2 {
    margin-bottom: 20px;
  }
  
  .form-group {
    margin-bottom: 15px;
  }
  
  .form-group label {
    display: block;
    margin-bottom: 5px;
  }
  
  .form-group input, .form-group select {
    width: 100%;
    padding: 10px;
    border-radius: 5px;
    border: 1px solid #ccc;
  }
  
  .form-group button {
    width: 100%;
    padding: 10px;
    background-color: #ff6347; /* Tomato */
    color: white;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    transition: background-color 0.3s;
  }
  
  .form-group button:hover {
    background-color: #ff4500; /* OrangeRed */
  }
  </style>
  