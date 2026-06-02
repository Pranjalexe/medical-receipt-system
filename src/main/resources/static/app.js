// State
const state = {
    token: localStorage.getItem('jwt') || null,
    user: JSON.parse(localStorage.getItem('user')) || null
};

// UI Elements
const DOM = {
    authView: document.getElementById('authView'),
    sidebar: document.getElementById('sidebar'),
    dashboardViews: document.getElementById('dashboardViews'),
    userName: document.getElementById('userName'),
    userRole: document.getElementById('userRole'),
    navMenu: document.getElementById('navMenu'),
    toastContainer: document.getElementById('toastContainer')
};

// Initialization
document.addEventListener('DOMContentLoaded', () => {
    initAuthTabs();
    initRoleSelect();
    initForms();
    
    if (state.token && state.user) {
        showDashboard();
    }
});

// Toast Notifications
function showToast(message, type = 'success') {
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    const icon = type === 'success' ? 'ri-checkbox-circle-line' : 'ri-error-warning-line';
    toast.innerHTML = `<i class="${icon}"></i> <span>${message}</span>`;
    DOM.toastContainer.appendChild(toast);
    
    setTimeout(() => {
        toast.style.opacity = '0';
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

// API Helper
async function apiCall(endpoint, method = 'GET', body = null) {
    const headers = {
        'Content-Type': 'application/json'
    };
    if (state.token) {
        headers['Authorization'] = `Bearer ${state.token}`;
    }

    try {
        const response = await fetch(endpoint, {
            method,
            headers,
            body: body ? JSON.stringify(body) : null
        });
        
        const data = await response.json();
        
        if (!response.ok) {
            throw new Error(data.message || 'API request failed');
        }
        return data;
    } catch (error) {
        showToast(error.message, 'error');
        if (error.message.includes('JWT') || error.message.includes('Authentication')) {
            logout();
        }
        throw error;
    }
}

// Auth UI Logic
function initAuthTabs() {
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.addEventListener('click', (e) => {
            document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
            document.querySelectorAll('.auth-form').forEach(f => f.classList.remove('active'));
            
            e.target.classList.add('active');
            document.getElementById(e.target.dataset.target).classList.add('active');
        });
    });
}

function initRoleSelect() {
    const roleSelect = document.getElementById('regRole');
    roleSelect.addEventListener('change', (e) => {
        document.querySelectorAll('.role-fields').forEach(f => f.classList.remove('active'));
        if (e.target.value === 'ROLE_DOCTOR') {
            document.getElementById('doctorFields').classList.add('active');
        } else {
            document.getElementById('patientFields').classList.add('active');
        }
    });
}

// Form Handlers
function initForms() {
    // Login
    document.getElementById('loginForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        try {
            const data = await apiCall('/api/auth/login', 'POST', {
                email: document.getElementById('loginEmail').value,
                password: document.getElementById('loginPassword').value
            });
            handleAuthSuccess(data.data);
        } catch (err) {}
    });

    // Register
    document.getElementById('registerForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        const role = document.getElementById('regRole').value;
        const payload = {
            fullName: document.getElementById('regName').value,
            email: document.getElementById('regEmail').value,
            password: document.getElementById('regPassword').value,
            role: role
        };

        if (role === 'ROLE_DOCTOR') {
            payload.specialization = document.getElementById('regSpecialization').value;
            payload.licenseNumber = document.getElementById('regLicense').value;
        } else {
            payload.dateOfBirth = document.getElementById('regDob').value;
            payload.bloodGroup = document.getElementById('regBloodGroup').value;
            payload.address = document.getElementById('regAddress').value;
        }

        try {
            const data = await apiCall('/api/auth/register', 'POST', payload);
            handleAuthSuccess(data.data);
        } catch (err) {}
    });

    // Logout
    document.getElementById('logoutBtn').addEventListener('click', logout);

    // Add Prescription Item
    document.getElementById('addPrescItemBtn').addEventListener('click', addPrescriptionItemRow);

    // Create Prescription
    document.getElementById('createPrescriptionForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        const items = [];
        document.querySelectorAll('.presc-item-row').forEach(row => {
            items.push({
                drugName: row.querySelector('.i-drug').value,
                dosage: row.querySelector('.i-dosage').value,
                frequency: row.querySelector('.i-freq').value,
                duration: row.querySelector('.i-dur').value,
                quantity: parseInt(row.querySelector('.i-qty').value)
            });
        });

        if (items.length === 0) {
            showToast('Add at least one medication', 'error');
            return;
        }

        try {
            await apiCall('/api/doctors/prescriptions', 'POST', {
                patientId: parseInt(document.getElementById('prescPatientId').value),
                notes: document.getElementById('prescNotes').value,
                items: items
            });
            showToast('Prescription issued successfully!');
            document.getElementById('createPrescriptionForm').reset();
            document.getElementById('prescriptionItemsList').innerHTML = '';
            showView('view-list-prescriptions');
        } catch (err) {}
    });

    // Generate Receipt Form
    document.getElementById('generateReceiptForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        const prescId = document.getElementById('receiptPrescId').value;
        try {
            await apiCall('/api/doctors/receipts/generate', 'POST', {
                prescriptionId: parseInt(prescId),
                discount: parseFloat(document.getElementById('receiptDiscount').value),
                taxRate: parseFloat(document.getElementById('receiptTax').value),
                paymentMethod: document.getElementById('receiptPaymentMethod').value
            });
            showToast('Receipt generated successfully!');
            showView('view-list-receipts');
        } catch (err) {}
    });
}

// Dynamic Prescription Items
function addPrescriptionItemRow() {
    const list = document.getElementById('prescriptionItemsList');
    const row = document.createElement('div');
    row.className = 'presc-item-row';
    row.innerHTML = `
        <div class="form-group mb-0"><input type="text" class="i-drug" placeholder="Drug (e.g. Advil)" required></div>
        <div class="form-group mb-0"><input type="text" class="i-dosage" placeholder="Dosage" required></div>
        <div class="form-group mb-0"><input type="text" class="i-freq" placeholder="Freq" required></div>
        <div class="form-group mb-0"><input type="text" class="i-dur" placeholder="Duration" required></div>
        <div class="form-group mb-0"><input type="number" class="i-qty" placeholder="Qty" required min="1"></div>
        <button type="button" class="btn btn-outline-danger btn-sm" onclick="this.parentElement.remove()"><i class="ri-delete-bin-line"></i></button>
    `;
    list.appendChild(row);
}

// Authentication flow
function handleAuthSuccess(userData) {
    state.token = userData.token;
    state.user = userData;
    localStorage.setItem('jwt', userData.token);
    localStorage.setItem('user', JSON.stringify(userData));
    showToast('Authentication successful');
    showDashboard();
}

function logout() {
    state.token = null;
    state.user = null;
    localStorage.removeItem('jwt');
    localStorage.removeItem('user');
    DOM.authView.classList.remove('hidden');
    DOM.sidebar.classList.add('hidden');
    DOM.dashboardViews.classList.add('hidden');
    showToast('Logged out');
}

// Dashboard rendering
function showDashboard() {
    DOM.authView.classList.add('hidden');
    DOM.sidebar.classList.remove('hidden');
    DOM.dashboardViews.classList.remove('hidden');
    
    DOM.userName.textContent = state.user.fullName;
    DOM.userRole.textContent = state.user.role.replace('ROLE_', '');

    renderNav();
}

function renderNav() {
    let navHtml = '';
    if (state.user.role === 'ROLE_DOCTOR') {
        navHtml = `
            <a class="nav-item active" onclick="showView('view-create-prescription', this)"><i class="ri-file-add-line"></i> Issue Prescription</a>
            <a class="nav-item" onclick="showView('view-list-prescriptions', this)"><i class="ri-file-list-3-line"></i> Prescriptions</a>
            <a class="nav-item" onclick="showView('view-list-receipts', this)"><i class="ri-wallet-3-line"></i> Receipts</a>
        `;
        showView('view-create-prescription');
    } else {
        navHtml = `
            <a class="nav-item active" onclick="showView('view-list-prescriptions', this)"><i class="ri-file-list-3-line"></i> My Prescriptions</a>
            <a class="nav-item" onclick="showView('view-list-receipts', this)"><i class="ri-wallet-3-line"></i> My Receipts</a>
        `;
        showView('view-list-prescriptions');
    }
    DOM.navMenu.innerHTML = navHtml;
}

function showView(viewId, navElement = null) {
    document.querySelectorAll('.view-section').forEach(v => v.classList.remove('active'));
    document.getElementById(viewId).classList.add('active');
    
    if (navElement) {
        document.querySelectorAll('.nav-item').forEach(n => n.classList.remove('active'));
        navElement.classList.add('active');
    }

    // Load data if needed
    if (viewId === 'view-list-prescriptions') fetchPrescriptions();
    if (viewId === 'view-list-receipts') fetchReceipts();
}

// Data Fetching
async function fetchPrescriptions() {
    try {
        const endpoint = state.user.role === 'ROLE_DOCTOR' ? '/api/doctors/prescriptions' : '/api/patients/prescriptions';
        const response = await apiCall(endpoint);
        const tbody = document.getElementById('prescriptionsTableBody');
        tbody.innerHTML = '';
        
        response.data.forEach(p => {
            const tr = document.createElement('tr');
            let actionBtn = '';
            if (state.user.role === 'ROLE_DOCTOR') {
                actionBtn = `<button class="btn btn-sm btn-outline" onclick="openGenerateReceipt(${p.id})">Generate Receipt</button>`;
            }
            
            tr.innerHTML = `
                <td>#${p.id}</td>
                <td>${p.issueDate || 'N/A'}</td>
                <td>${state.user.role === 'ROLE_DOCTOR' ? p.patientName : p.doctorName}</td>
                <td>${p.items.length} meds</td>
                <td>${actionBtn}</td>
            `;
            tbody.appendChild(tr);
        });
    } catch (err) {}
}

async function fetchReceipts() {
    try {
        const endpoint = state.user.role === 'ROLE_DOCTOR' ? '/api/doctors/receipts' : '/api/patients/receipts';
        const response = await apiCall(endpoint);
        const tbody = document.getElementById('receiptsTableBody');
        tbody.innerHTML = '';
        
        response.data.forEach(r => {
            const tr = document.createElement('tr');
            const statusClass = r.status === 'PENDING' ? 'badge-pending' : 'badge-paid';
            tr.innerHTML = `
                <td><strong>${r.receiptNumber}</strong></td>
                <td>${r.generatedAt.substring(0, 10)}</td>
                <td>$${r.netAmount.toFixed(2)}</td>
                <td><span class="badge ${statusClass}">${r.status}</span></td>
                <td>${r.paymentMethod}</td>
            `;
            tbody.appendChild(tr);
        });
    } catch (err) {}
}

function openGenerateReceipt(prescId) {
    document.getElementById('receiptPrescId').value = prescId;
    document.getElementById('receiptPrescIdDisplay').value = `Prescription #${prescId}`;
    showView('view-generate-receipt');
}
