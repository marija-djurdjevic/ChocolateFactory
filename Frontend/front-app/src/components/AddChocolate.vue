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
          <label for="gs">GS:</label>
          <input type="text" id="gs" v-model="location.gs" required />
        </div>
        <div class="field-group">
          <label for="gd">GD:</label>
          <input type="text" id="gd" v-model="location.gd" required />
        </div>
        <div class="field-group">
          <label for="address">Address:</label>
          <input type="text" id="address" v-model="location.address" required />
        </div>
        <div class="field-group">
          <label for="city">City:</label>
          <input type="text" id="city" v-model="location.city" required />
        </div>
      </div>
      <div class="form-group">
        <label for="worktime">Work time:</label>
        <input type="text" id="worktime" v-model="factory.worktime" required />
      </div>
      <div class="form-group">
        <label for="image">Logo:</label>
        <input type="file" @change="onFileSelected" />
      </div>
      <div class="form-group">
        <label for="manager">Manager:</label>
        <input type="text" id="manager" v-model="factory.manager" required />            
      </div>
      <button type="submit">Add Factory</button>
    </form>
  </div>
</template>

  
  <script setup>
  import { ref } from 'vue';
  import { useRouter, useRoute } from 'vue-router';
  import axios from 'axios';
  import EventBus from '../event-bus';
  
  const route = useRoute();
  const router = useRouter();
  const factoryId = route.query.factoryId;
  
  const chocolate = ref({
    name: '',
    chocolateSort: 'regular',
    chocolateType: 'dark',
    factoryId: factoryId,
    gramsOfChocolate: 0,
    chocolateDescription: '',
    imagePath: '',
    imageString: '',
    isAvailable: false,
    amountOfChocolate: 0,
  });
  
  const selectedFile = ref(null);
  
  function onFileSelected(event) {
    selectedFile.value = event.target.files[0];
  }
  
  function submitForm() {
    if (selectedFile.value) {
      const reader = new FileReader();
      reader.onload = (e) => {
        chocolate.value.imageString = e.target.result.split(",")[1];
        saveChocolate();
      }
      reader.readAsDataURL(selectedFile.value);
    }
    
  }
  
  function saveChocolate() {
    console.log("Sada je string " + chocolate.value.imageString);
    axios.post('http://localhost:8080/WebShopAppREST/rest/chocolates/save', chocolate.value)
      .then(response => {
        alert('Chocolate added successfully!');
        router.push('/');
      })
      .catch(error => {
        console.error('Error adding chocolate:', error);
      });
  }
  </script>
  
  <style>
  .add-chocolate {
    max-width: 600px;
    margin: 50px auto;
    padding: 20px;
    background-color: #ffe4b5;
    border-radius: 10px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  }
  
  .add-chocolate h1 {
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
  