<template>
    <div class="add-worker-container">
      <header class="status-bar">
        <h1>Add Worker</h1>
      </header>
      <div class="add-worker-form">
        <h2>Create a Worker Account</h2>
        <form @submit.prevent="addWorker">
          <div class="form-group">
            <label for="username">Username</label>
            <input type="text" id="username" v-model="worker.username" required>
          </div>
          <div class="form-group">
            <label for="password">Password</label>
            <input type="password" id="password" v-model="worker.password" required>
          </div>
          <div class="form-group">
            <label for="confirmPassword">Confirm Password</label>
            <input type="password" id="confirmPassword" v-model="confirmPassword" required>
          </div>
          <div class="form-group">
            <label for="name">First Name</label>
            <input type="text" id="name" v-model="worker.name" required>
          </div>
          <div class="form-group">
            <label for="surname">Last Name</label>
            <input type="text" id="surname" v-model="worker.surname" required>
          </div>
          <div class="form-group">
            <label for="gender">Gender</label>
            <select id="gender" v-model="worker.gender" required>
              <option value="male">Male</option>
              <option value="female">Female</option>
            </select>
          </div>
          <div class="form-group">
            <label for="birthDate">Date of Birth</label>
            <input type="date" id="birthDate" v-model="worker.birthDate" required>
          </div>
          <div class="form-group">
            <button type="submit">Add Worker</button>
          </div>
          <div v-if="errorMessage" class="error-message">
          {{ errorMessage }}
        </div>
        </form>
      </div>
    </div>
  </template>
  
  <script setup>
import { ref } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';

const confirmPassword = ref('');
const worker = ref({
  username: '',
  password: '',
  name: '',
  surname: '',
  gender: '',
  birthDate: ''
});

const router = useRouter();
const factoryId = router.currentRoute.value.query.factoryId;
const errorMessage = ref('');


function addWorker() {
  errorMessage.value = ''; // Reset error message

  if (worker.value.password !== confirmPassword.value) {
    alert('Passwords do not match!');
    return;
  }

  const token = localStorage.getItem('token');
  const config = {
    headers: {
      Authorization: `Bearer ${token}`
    }
  };

  axios.post(`http://localhost:8080/WebShopAppREST/rest/factories/${factoryId}/addWorker`, worker.value, config)
    .then(response => {
      alert('Worker added successfully!');
      router.push('/');
    })
    .catch(error => {
      if (error.response && error.response.status === 403) {
        errorMessage.value = 'You do not have permission to add a worker to this factory.';
      } else {
        errorMessage.value = 'An error occurred while adding the worker. Please try again later.';
      }
      console.error('Error adding worker:', error);
    });
}
</script>
  
  
  <style>
  /* Your existing styles for the form */
  .add-worker-container {
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
  
  .add-worker-form {
    background-color: #ffe4b5; /* Bisque */
    border-radius: 10px;
    padding: 20px;
    max-width: 400px;
    margin: 0 auto;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  }
  
  .add-worker-form h2 {
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
  .error-message {
  color: red;
  margin-top: 15px;
}
  </style>
  