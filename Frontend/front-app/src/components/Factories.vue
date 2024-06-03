<template>
    <div class="factory-display">
      <div v-for="factory in factories" :key="factory.id" class="factory-card">
        <div class="factory-info">
          <div class="factory-logo">
            <img :src="factory.image" alt="Factory Logo" />
          </div>
          <div class="factory-details">
            <h3>{{ factory.name }}</h3>
            <p>{{ factory.address }}</p>
            <p v-if="factory.status">Status: Open</p>
            <p v-else>Status: Closed</p>
            <p>Average Grade: {{ factory.grade }}</p>
          </div>
        </div>
      </div>
    </div>
  </template>
  

<script setup>
    import axios from 'axios';
    import { onMounted, ref } from 'vue';
    import { useRouter } from 'vue-router';

    const router = useRouter();

    const factories = ref([]);
    
    const error = ref("");

    onMounted(() => {
	    loadFactories();
    })

    function loadFactories() {
        axios.get('http://localhost:8080/WebShopAppREST/rest/factories/')
        .then(response => {
            factories.value = response.data
        });
    }
   
</script>

<style scoped>
  .factory-display {
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    background-color: darksalmon;
  }
  
  .factory-card {
    width: 300px;
    margin: 20px;
    border: 1px solid #ccc;
    border-radius: 5px;
    overflow: hidden;
  }
  
  .factory-info {
    display: flex;
    padding: 10px;
  }
  
  .factory-logo img {
    width: 80px;
    height: 80px;
    object-fit: cover;
    border-radius: 50%;
    margin-right: 10px;
  }
  
  .factory-details {
    flex-grow: 1;
  }
  
  .factory-details h3 {
    margin: 0;
    margin-bottom: 5px;
  }
  
  .factory-details p {
    margin: 0;
    margin-bottom: 5px;
  }
  </style>
  