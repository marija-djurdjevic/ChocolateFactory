<template>
    <div class="users-display">
      <header class="status-bar">
        <h1>Users</h1>
      </header>
      <div class="search-bar">
        <input v-model="searchName" placeholder="Name">
        <input v-model="searchSurname" placeholder="Surname">
        <input v-model="searchUsername" placeholder="Username">
        <button @click="searchUsers">Search</button>
      </div>
      <div class="filter-bar">
        <select v-model="selectedRole">
          <option value="">Select Role</option>
          <option value="Administrator">Administrator</option>
          <option value="Manager">Manager</option>
          <option value="Worker">Worker</option>
          <option value="Customer">Customer</option>
        </select>
        <select v-model="selectedType">
          <option value="">Select User Type</option>
          <option value="Golden">Golden</option>
          <option value="Silver">Silver</option>
          <option value="Bronze">Bronze</option>
        </select>
        <button @click="applyFilters">Filter</button>
        <select v-model="selectedSortBy">
          <option value="">Sort By</option>
          <option value="name">Name</option>
          <option value="surname">Surname</option>
          <option value="username">Username</option>
          <option value="points">Points</option>
          <option value="type">User Type</option>
        </select>
        <select v-model="selectedSortOrder">
          <option value="asc">Ascending</option>
          <option value="desc">Descending</option>
        </select>
        <button @click="applySorting">Sort</button>
      </div>
      <div class="user-cards">
        <div v-for="user in displayedUsers" :key="user.id" class="user-card">
          <div class="user-info">
            <h2>{{ user.username }}</h2>
            <p><strong>Name:</strong> {{ user.name }} {{ user.lastName }}</p>
            <p><strong>Surname:</strong> {{ user.surname }}</p>
            <p><strong>Birth Date:</strong> {{ user.birthDate }}</p>
            <p><strong>Gender:</strong> {{ user.gender }}</p>
            <p><strong>Role:</strong> {{ user.role }}</p>
            <p v-if="user.role === 'Customer'"><strong>Points:</strong> {{ user.points }}</p>
            <p v-if="user.role === 'Customer'"><strong>Type:</strong> {{ user.type }}</p>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  
  <script setup>
  import { ref } from 'vue';
import axios from 'axios';

const users = ref([]);
const customers = ref([]);
const displayedUsers = ref([]);
const searchName = ref('');
const searchSurname = ref('');
const searchUsername = ref('');
const selectedRole = ref('');
const selectedType = ref('');
const selectedSortBy = ref('');
const selectedSortOrder = ref('');

// Function to load users data
const loadUsers = async () => {
  try {
    const [usersResponse, customersResponse] = await Promise.all([
      axios.get('http://localhost:8080/WebShopAppREST/rest/users'),
      axios.get('http://localhost:8080/WebShopAppREST/rest/customers')
    ]);
    
    users.value = usersResponse.data;
    customers.value = customersResponse.data;

    // Combine user data with customer data
    users.value.forEach(user => {
      if (user.role === 'Customer') {
        const customer = customers.value.find(cust => cust.username === user.username);
        if (customer) {
          user.points = customer.points;
          user.type = determineCustomerType(customer.points);
        }
      }
    });

    applyFilters(); // Apply initial filters
  } catch (error) {
    console.error('Error loading users:', error);
  }
};

// Function to determine customer type based on points
const determineCustomerType = (points) => {
  if (points < 10) {
    return 'Bronze';
  } else if (points >= 10 && points < 20) {
    return 'Silver';
  } else {
    return 'Golden';
  }
};

// Function to filter users based on current criteria
const filterUsers = () => {
  displayedUsers.value = users.value.filter(user => {
    const nameMatch = searchName.value === '' || user.name.toLowerCase().includes(searchName.value.toLowerCase());
    const surnameMatch = searchSurname.value === '' || user.surname.toLowerCase().includes(searchSurname.value.toLowerCase());
    const usernameMatch = searchUsername.value === '' || user.username.toLowerCase().includes(searchUsername.value.toLowerCase());
    const roleMatch = selectedRole.value === '' || user.role === selectedRole.value;
    const typeMatch = selectedType.value === '' || (user.role === 'Customer' && user.type === selectedType.value);
    return nameMatch && surnameMatch && usernameMatch && roleMatch && typeMatch;
  });
};

// Function to sort users based on current criteria
const sortUsers = () => {
  if (selectedSortBy.value && selectedSortOrder.value) {
    displayedUsers.value.sort((a, b) => {
      const fieldA = a[selectedSortBy.value];
      const fieldB = b[selectedSortBy.value];
      
      // Check if sorting by points or type
      if (selectedSortBy.value === 'points' || selectedSortBy.value === 'type') {
        // Ensure only customers are sorted by these fields
        if (a.role !== 'Customer') return 1;
        if (b.role !== 'Customer') return -1;
      }

      if (selectedSortOrder.value === 'asc') {
        return String(fieldA).localeCompare(String(fieldB));
      } else {
        return String(fieldB).localeCompare(String(fieldA));
      }
    });
  }
};

// Function to trigger search action
const searchUsers = async () => {
  await loadUsers(); // Reload users data
};

// Function to trigger filter action
const applyFilters = () => {
  filterUsers(); // Apply filters
};

// Function to trigger sort action
const applySorting = () => {
  sortUsers(); // Apply sorting
};

// Load users data initially when component mounts
loadUsers();

  </script>
  
  <style scoped>
  .users-display {
    text-align: center;
    padding: 20px;
    min-height: 100vh;
    background-color: #dd6755;
  }
  
  .status-bar {
    background-color: #FFC9AD;
    color: #333;
    padding: 10px;
    margin-bottom: 20px;
  }
  
  .status-bar h1 {
    font-size: 2rem;
    margin: 0;
  }
  
  .search-bar {
    display: flex;
    justify-content: center;
    gap: 10px;
    margin-bottom: 20px;
  }
  
  .search-bar input {
    padding: 10px;
    border: 1px solid #ccc;
    border-radius: 5px;
    width: 150px;
  }
  
  .search-bar button {
    padding: 10px;
    background-color: #ff6347;
    color: white;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    transition: background-color 0.3s;
    width: 80px;
    height: 40px;
  }
  
  .search-bar button:hover {
    background-color: #ff4500;
  }
  
  .filter-bar {
    display: flex;
    justify-content: center;
    gap: 10px;
    margin-bottom: 20px;
  }
  
  .filter-bar select {
    padding: 8px;
    border-radius: 5px;
    border: 1px solid #ccc;
    width: 150px;
  }
  
  .filter-bar button {
    padding: 10px;
    background-color: #ff6347;
    color: white;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    transition: background-color 0.3s;
    width: 120px;
  }
  
  .filter-bar button:hover {
    background-color: #ff4500;
  }
  
  .user-cards {
    display: flex;
    flex-wrap: wrap;
    justify-content: space-around;
  }
  
  .user-card {
    width: 300px;
    margin: 20px;
    border: 1px solid #ccc;
    border-radius: 10px;
    overflow: hidden;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    transition: box-shadow 0.3s;
    background-color: #FFE4B5;
  }
  
  .user-card:nth-child(even) {
    background-color: #FFC9AD;
  }
  
  .user-card:hover {
    box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
  }
  
  .user-info {
    padding: 20px;
    text-align: left;
  }
  </style>
  