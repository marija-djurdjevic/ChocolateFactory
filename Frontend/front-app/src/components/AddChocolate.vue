<template>
    <div class="add-chocolate">
      <h1>Add Chocolate</h1>
      <form @submit.prevent="submitForm">
        <div class="form-group">
          <label for="name">Name:</label>
          <input type="text" id="name" v-model="chocolate.name" required />
        </div>
        <div class="form-group">
        <label for="price">Price:</label>
        <input type="number" step="0.01" v-model.number="chocolate.price" required />
      </div>
      <div class="form-group">
          <label for="chocolateSort">Chocolate Sort:</label>
          <input type="text" id="chocolateSort" v-model="chocolate.chocolateSort" required />
        </div>
        <div class="form-group">
        <label for="chocolateType">Chocolate Type:</label>
        <input type="text" id="chocolateType" v-model="chocolate.chocolateType" required />            
      </div>
        <div class="form-group">
          <label for="gramsOfChocolate">Grams of Chocolate:</label>
          <input type="number" id="gramsOfChocolate" v-model="chocolate.gramsOfChocolate" required />
        </div>
        <div class="form-group">
          <label for="chocolateDescription">Description:</label>
          <textarea id="chocolateDescription" v-model="chocolate.chocolateDescription" required></textarea>
        </div>
        <div class="form-group">
          <label for="image">Image:</label>
          <input type="file" @change="onFileSelected" />
        </div>
        <button type="submit">Add Chocolate</button>
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
  