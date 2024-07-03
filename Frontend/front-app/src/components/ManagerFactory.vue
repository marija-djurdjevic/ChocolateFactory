<template>
    <div>
      <h1>Fabrika: {{ factory.name }}</h1>
      <h2>Kupovine</h2>
      <ul>
        <li v-for="purchase in purchases" :key="purchase.id">
          Kupovina ID: {{ purchase.id }}, Cena: {{ purchase.price }}, Datum: {{ purchase.dateAndTime }}
        </li>
      </ul>
    </div>
  </template>
  
  <script>
  import axios from 'axios';
  
  export default {
    data() {
      return {
        factory: {},
        purchases: []
      };
    },
    created() {
      this.loadFactoryData();
    },
    methods: {
      async loadFactoryData() {
        try {
          const factoryId = this.$route.params.factoryId;
          const response = await axios.get(`/api/purchases/factory/${factoryId}`);
          this.purchases = response.data;
          // Pretpostavljamo da postoji API za dohvat podataka o fabrici
          const factoryResponse = await axios.get(`/api/factories/${factoryId}`);
          this.factory = factoryResponse.data;
        } catch (error) {
          console.error('Error loading factory data:', error);
        }
      }
    }
  };
  </script>
  
  <style scoped>
  /* Dodajte stilove po potrebi */
  </style>
  