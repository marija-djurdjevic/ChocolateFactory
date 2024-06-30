<template>
    <div class="add-factory">
      <h1>Add Factory</h1>
      <form @submit.prevent="submitForm">
        <div class="form-group">
          <label for="name">Name:</label>
          <input type="text" id="name" v-model="factory.name" required />
        </div>
        <label>Location:</label>
        <MapComponent @location-selected="updateLocation" />
        <div class="form-group">
          <label>Selected Location:</label>
          <div class="locationparams">{{ selectedLocation.address }}</div>
          <div class="locationparams">{{ selectedLocation.longitude }}, {{ selectedLocation.latitude }}</div>
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
          <div v-if="managers.length > 0">
            <select id="manager" v-model="selectedManager" required>
              <option v-for="manager in managers" :key="manager.id" :value="manager">
                {{ manager.name }} {{ manager.surname }}
              </option>
            </select>
          </div>
          <div v-else>
            <p>No managers available. <br/>
            <button @click.prevent="showManagerModal = true" class="regManButton">Register new manager</button></p>
          </div>
        </div>
        <button type="submit" class="button">Add Factory</button>
      </form>
  
      <Modal :isVisible="showManagerModal" @close="showManagerModal = false">
        <ManagerRegistration @manager-added="handleManagerAdded" />
      </Modal>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue';
  import axios from 'axios';
  import Modal from './Modal.vue';
  import ManagerRegistration from './ManagerRegistration.vue';
  import MapComponent from './MapComponent.vue';
  import { useRouter } from 'vue-router';
  
  const factory = ref({
    name: '',
    status: false,
    location: -1,
    worktime: '',
    image: '',
    grade: 0
  });
  const selectedLocation = ref({
    longitude: '',
    latitude: '',
    address: ''
  });
  const workTimeFrom = ref('');
  const workTimeTo = ref('');
  const managers = ref([]);
  const selectedFile = ref(null);
  const selectedManager = ref(null);
  const showManagerModal = ref(false);
  
  const router = useRouter();
  
  onMounted(() => {
    fetchManagers();
  });
  
  function fetchManagers() {
    axios.get('http://localhost:8080/WebShopAppREST/rest/managers/findAvailable')
      .then(response => {
        managers.value = response.data;
      })
      .catch(error => {
        console.error('Error fetching managers:', error);
      });
  }
  
  function updateLocation(location) {
    selectedLocation.value = location;
  }
  
  function onFileSelected(event) {
    selectedFile.value = event.target.files[0];
  }
  
  function submitForm() {
    if (selectedFile.value) {
      const reader = new FileReader();
      reader.onload = (e) => {
        factory.value.imageString = e.target.result.split(",")[1];
        factory.value.worktime = workTimeFrom.value + "-" + workTimeTo.value;
        saveLocation();
      };
      reader.readAsDataURL(selectedFile.value);
    } else {
      factory.value.worktime = workTimeFrom.value + "-" + workTimeTo.value;
      saveLocation();
    }
  }
  
  function saveLocation() {
    axios.post('http://localhost:8080/WebShopAppREST/rest/locations/save', selectedLocation.value)
      .then(response => {
        factory.value.location = response.data.id;
        saveFactory();
      })
      .catch(error => {
        console.error('Error saving location:', error);
      });
  }
  
  function saveFactory() {
    const token = localStorage.getItem('token');
    axios.post('http://localhost:8080/WebShopAppREST/rest/factories/save', factory.value, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
    .then(response => {
      if (response.status === 200) {
        alert('Factory added successfully!');
        updateManager(response.data.id);
        router.push('/');
      } else {
        console.error('Error adding factory:', response.data);
      }
    })
    .catch(error => {
      console.error('Error adding factory:', error);
    });
  }
  
  function updateManager(factoryId) {
    const managerToUpdate = selectedManager.value;
    if (managerToUpdate) {
      managerToUpdate.factoryId = factoryId;
      axios.post(`http://localhost:8080/WebShopAppREST/rest/managers/edit`, managerToUpdate)
        .then(response => {
          console.log('Manager updated successfully');
        })
        .catch(error => {
          console.error('Error updating manager:', error);
        });
    }
  }
  
  function handleManagerAdded(manager) {
    managers.value.push(manager);
    selectedManager.value = manager;
    showManagerModal.value = false;
  }
  </script>
  
  <style scoped>
  .add-factory {
    max-width: 700px; 
    margin: 20px auto;
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
  
  .form-group input,
  .form-group select,
  .form-group textarea {
    width: 100%;
    padding: 10px; 
    box-sizing: border-box;
  }
  
  .locationparams {
    width: 100%;
    font-size: medium; 
    margin-bottom: 10px; 
  }
  
  .work-time-fields {
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
  
  .button {
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
  
  .button:hover {
    background-color: #bf5640;
  }
  
  .regManButton {
    display: block;
    width: 50%;
    padding: 10px;
    background-color: #dd6755;
    color: #fff;
    border: none;
    border-radius: 5px;
    font-size: 14px;
    cursor: pointer;
    margin-top: 10px;
  }
  
  .regManButton:hover {
    background-color: #bf5640;
  }
  </style>
  