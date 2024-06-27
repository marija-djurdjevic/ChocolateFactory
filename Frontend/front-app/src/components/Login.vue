<template>
    <div class="login-container">
      <header class="status-bar">
        <h1>Login</h1>
      </header>
      <div class="login-form">
        <h2>Sign In</h2>
        <form @submit.prevent="login">
          <div class="form-group">
            <label for="username">Username</label>
            <input type="text" id="username" v-model="username" required>
          </div>
          <div class="form-group">
            <label for="password">Password</label>
            <input type="password" id="password" v-model="password" required>
          </div>
          <div class="form-group">
            <button type="submit">Login</button>
          </div>
        </form>
        <p>Don't have an account? <a href="#" @click="goToRegister">Register</a></p>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue';
  import { useRouter } from 'vue-router';
  import axios from 'axios';
  import Cookies from 'js-cookie';
  
  const username = ref('');
  const password = ref('');
  const router = useRouter();
  

  const axiosInstance = axios.create({
  baseURL: 'http://localhost:8080/WebShopAppREST/rest',
  withCredentials: true, // Omogućava slanje kolačića
});
  
  function login() {
    axiosInstance.post('http://localhost:8080/WebShopAppREST/rest/users/logging?username='+username.value +'&password='+ password.value, 
    )
    .then(response => {
      if (response.data) {
      // Sačuvaj token u kolačić (ako koristiš JWT)
      const token = response.data.token;
      Cookies.set('token', token, { expires: 7, path: '' }); // Sačuvaj kolačić na 7 dana

      alert('Login successful!');
      console.log('Token:', Cookies.get('token')); // Prikaži token u konzoli
      router.go(-1); 
    } else {
      throw new Error('Invalid credentials');
    }
    })
    .catch(error => {
      console.error('Login failed:', error);
      alert('Login failed, please check your credentials');
    });
}


  
  function goToRegister() {
    router.push('/register');
  }
  </script>
  
  <style>
  .login-container {
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
  
  .login-form {
    background-color: #ffe4b5; /* Bisque */
    border-radius: 10px;
    padding: 20px;
    max-width: 400px;
    margin: 0 auto;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  }
  
  .login-form h2 {
    margin-bottom: 20px;
  }
  
  .form-group {
    margin-bottom: 15px;
  }
  
  .form-group label {
    display: block;
    margin-bottom: 5px;
  }
  
  .form-group input {
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
  