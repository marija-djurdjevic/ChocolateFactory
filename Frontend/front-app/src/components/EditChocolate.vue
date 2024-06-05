<template>
    <div class="edit-chocolate">
      <h1>Edit Chocolate</h1>
      <form @submit.prevent="submitForm">
        <div class="form-group">
          <label for="name">Name:</label>
          <input type="text" id="name" v-model="chocolate.name" />
        </div>
        <div class="form-group">
          <label for="price">Price:</label>
          <input type="number" step="0.01" v-model.number="chocolate.price" />
        </div>
        <div class="form-group">
          <label for="chocolateSort">Chocolate Sort:</label>
          <input type="text" id="chocolateSort" v-model="chocolate.chocolateSort" />
        </div>
        <div class="form-group">
          <label for="chocolateType">Chocolate Type:</label>
          <input type="text" id="chocolateType" v-model="chocolate.chocolateType" />            
        </div>
        <div class="form-group">
          <label for="amountOfChocolate">Amount of Chocolate:</label>
          <input type="number" id="amountOfChocolate" v-model="chocolate.amountOfChocolate" />
        </div>
        <div class="form-group checkbox-group">
          <label for="isAvailable">Available:</label>
          <input type="checkbox" id="isAvailable" v-model="chocolate.available" />
        </div>
        <div class="form-group">
          <label for="gramsOfChocolate">Grams of Chocolate:</label>
          <input type="number" id="gramsOfChocolate" v-model="chocolate.gramsOfChocolate"  />
        </div>
        <div class="form-group">
          <label for="chocolateDescription">Description:</label>
          <textarea id="chocolateDescription" v-model="chocolate.chocolateDescription"></textarea>
        </div>
        <div class="form-group">
          <label for="image">Image:</label>
          <input type="file" @change="onFileSelected" />
        </div>
        <button type="submit">Save Changes</button>
      </form>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import axios from 'axios';
  
  const route = useRoute();
  const router = useRouter();
  const factoryId = route.query.factoryId;
  const chocolate = ref(JSON.parse(route.query.chocolate));
  
  const selectedFile = ref(null);
  
  function onFileSelected(event) {
    selectedFile.value = event.target.files[0];
  }
  
  function submitForm() {
    console.log("Before sending: ", chocolate.value); // Dodano za debug
    if (selectedFile.value) {
      const reader = new FileReader();
      reader.onload = (e) => {
        chocolate.value.imageString = e.target.result.split(",")[1];
        saveChocolate();
      }
      reader.readAsDataURL(selectedFile.value);
    } else {
      saveChocolate();
    }
  }
  
  function saveChocolate() {
    axios.post(`http://localhost:8080/WebShopAppREST/rest/chocolates/edit`, chocolate.value)
      .then(response => {
        alert('Chocolate updated successfully!');
        router.push(`/factory/${factoryId}/chocolates`);
      })
      .catch(error => {
        console.error('Error updating chocolate:', error);
      });
  }
  </script>
  
  <style>
  .edit-chocolate {
    max-width: 600px;
    margin: 50px auto;
    padding: 20px;
    background-color: #ffe4b5;
    border-radius: 10px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  }
  
  .edit-chocolate h1 {
    text-align: center;
    margin-bottom: 20px;
  }
  
  .checkbox-group {
    display: flex;
    align-items: center;
  }
  
  .checkbox-group label {
    margin-right: 10px;
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