<template>
    <div class="manager-registration">
      <h2>Register Manager</h2>
      <form @submit.prevent="submitForm">
        <div class="form-group">
          <label for="username">Username:</label>
          <input type="text" id="username" v-model="manager.username" required />
        </div>
        <div class="form-group">
          <label for="password">Password:</label>
          <input type="password" id="password" v-model="manager.password" required />
        </div>
        <div class="form-group">
            <label for="confirmPassword">Confirm Password</label>
            <input type="password" id="confirmPassword" v-model="confirmPassword" required>
        </div>
        <div class="form-group">
          <label for="name">Name:</label>
          <input type="text" id="name" v-model="manager.name" required />
        </div>
        <div class="form-group">
          <label for="surname">Surname:</label>
          <input type="text" id="surname" v-model="manager.surname" required />
        </div>
        <div class="form-group">
          <label for="gender">Gender:</label>
          <select id="gender" v-model="manager.gender" required>
            <option value="male">Male</option>
            <option value="female">Female</option>
          </select>
        </div>
        <div class="form-group">
          <label for="birthDate">Birth Date:</label>
          <input type="date" id="birthDate" v-model="manager.birthDate" required />
        </div>
        <button type="submit">Register</button>
      </form>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue';
  import axios from 'axios';
  import { useRouter } from 'vue-router';
  
  const emit = defineEmits(['manager-added']);
  
  const manager = ref({
    username: '',
    password: '',
    name: '',
    surname: '',
    gender: '',
    birthDate: ''
  });
  
  const router = useRouter();
  
  function submitForm() {
    if (manager.value.password !== confirmPassword.value) {
    alert('Passwords do not match!');
    return;
  }
    axios.post('http://localhost:8080/WebShopAppREST/rest/users/saveManager', manager.value)
      .then(response => {
        if (response.status === 200) {
          alert('Manager registered successfully!');
          const newManager = response.data;
          newManager.factoryId = null;
          emit('manager-added', newManager); 
        } else {
          console.error('Error registering manager:', response.data);
        }
      })
      .catch(error => {
        console.error('Error registering manager:', error);
      });
  }
  </script>
  
  <style scoped>
.status-bar {
  background-color: #FFC9AD; /* Bisque */
  color: #333;
  padding: 10px;
  margin-bottom: 20px;
}

.status-bar h1 {
  text-align: center;
  margin-bottom: 20px;
}

.manager-registration {
  background-color: #ffe4b5; /* Bisque */
  border-radius: 10px;
  padding: 20px;
  max-width: 400px;
  margin: 0 auto;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.manager-registration h2 {
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
  