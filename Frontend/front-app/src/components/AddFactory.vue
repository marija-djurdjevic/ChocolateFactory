<template>
    <div class="add-factory">
      <h1>Add Factory</h1>
      <form @submit.prevent="submitForm">
        <div class="form-group">
          <label for="name">Name:</label>
          <input type="text" id="name" v-model="factory.name" required />
        </div>
        <label>Location:</label>
        <div class="location-fields">
          <div class="field-group">
            <label for="longitude">Longitude:</label>
            <input type="text" id="longitude" v-model="location.longitude" required />
          </div>
          <div class="field-group">
            <label for="latitude">Latitude:</label>
            <input type="text" id="latitude" v-model="location.latitude" required />
          </div>
          <div class="field-group">
            <label for="address">Address:</label>
            <input type="text" id="address" v-model="location.address" required />
          </div>
        </div>
        <div class="form-group">
          <label>Work time:</label>
          <div class="work-time-fields">
            <div class="field-group">
              <label for="from">From:</label>
              <input type="time" id="from" v-model="workTimeFrom" required />
            </div>
            <div class="field-group">
              <label for="to">To:</label>
              <input type="time" id="to" v-model="workTimeTo" required />
            </div>
          </div>
        </div>
        <div class="form-group">
          <label for="image">Logo:</label>
          <input type="file" @change="onFileSelected" />
        </div>
        <div class="form-group">
          <label for="manager">Manager:</label>
          <select id="manager" v-model="selectedManager" required>
            <option v-for="manager in managers" :key="manager.id" :value="manager.id">
              {{ manager.name }} {{ manager.surname }}
            </option>
          </select>
        </div>
        <button type="submit">Add Factory</button>
      </form>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue';
  import { useRouter } from 'vue-router';
  import axios from 'axios';
  import { onMounted } from 'vue';
  
  const router = useRouter();
  
  const factory = ref({
    name: '',
    status: false,
    locationId: -1,
    workTime: '',
    image: '',
    grade: 0,
    managerId: -1
  });
  
  const location = ref({
    longitude: '',
    latitude: '',
    address: ''
  });
  
  const workTimeFrom = ref('');
  const workTimeTo = ref('');
  const managers = ref([]);
  const selectedFile = ref(null);
  const selectedManager = ref(null);
  
  onMounted(() => {
    fetchManagers();
  });
  
  function fetchManagers() {
    axios.get('http://localhost:8080/WebShopAppREST/rest/managers/')
      .then(response => {
        managers.value = response.data;
      })
      .catch(error => {
        console.error('Error fetching managers:', error);
      });
  }
  
  function onFileSelected(event) {
    selectedFile.value = event.target.files[0];
  }
  
  function submitForm() {
    if (selectedFile.value) {
      const reader = new FileReader();
      reader.onload = (e) => {
        saveFactory();
      }
      reader.readAsDataURL(selectedFile.value);
    } else {
      saveFactory();
    }
  }
  
  function saveFactory() {
    const token = localStorage.getItem('token'); // Pretpostavljamo da token čuvate u localStorage
    axios.post('http://localhost:8080/WebShopAppREST/rest/factories/save', factory.value, {
        headers: {
        'Authorization': `Bearer ${token}`
        }
    })
    .then(response => {
        alert('Factory added successfully!');
        router.push('/');
    })
    .catch(error => {
        console.error('Error adding factory:', error);
    });
   }
  
</script>
  
  <style>
  .add-factory {
    max-width: 600px;
    margin: 50px auto;
    padding: 20px;
    background-color: #ffe4b5;
    border-radius: 10px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  }
  
  .add-factory h1 {
    text-align: center;
    margin-bottom: 20px;
  }
  
  .form-group {
    margin-bottom: 15px;
  }
  
  .form-group label {
    display: block;
    margin-bottom: 5px;
  }
  
  .form-group input, .form-group select, .form-group textarea {
    width: 100%;
    padding: 8px;
    box-sizing: border-box;
  }
  
  .location-fields {
    display: flex;
    justify-content: space-between;
    margin-bottom: 15px;
  }
  
  .field-group {
    flex: 1;
    margin-right: 10px;
  }
  
  .field-group:last-child {
    margin-right: 0;
  }
  
  .work-time-fields {
    display: flex;
    justify-content: space-between;
    margin-bottom: 15px;
  }
  
  button {
    display: block;
    width: 100%;
    padding: 10px;
    background-color: #dd6755;
    color: #fff;
    border: none;
    border-radius: 5px;
    font-size: 16px;
    cursor: pointer;
  }
  
  button:hover {
    background-color: #bf5640;
  }
  </style>
  