function showMessage(message, type) {
    const alertContainer = document.getElementById('alert-container');
    const alert = document.createElement('div');
    alert.className = `alert alert-${type} alert-dismissible fade show`;
    alert.role = 'alert';
    alert.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    `;
    alertContainer.appendChild(alert);

    // auto hide alert
    setTimeout(() => {
        alert.classList.remove('show');
        alert.classList.add('fade');
        setTimeout(() => {
            alert.remove();
        }, 150);
    }, 5000);
}

function showSuccessMessage(message) {
    showMessage(message, 'success');
}

function showErrorMessage(message) {
    showMessage(message, 'danger');
}

function formatDateToNZTime(gmtDateStr) {
    const date = new Date(gmtDateStr);

    // Extract day, month, year, hour, minute, and second
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0'); // Months are zero-indexed
    const year = date.getFullYear();

    let hour = date.getHours();
    const minute = String(date.getMinutes()).padStart(2, '0');
    const second = String(date.getSeconds()).padStart(2, '0');

    // Determine AM or PM
    const ampm = hour >= 12 ? 'PM' : 'AM';
    hour = hour % 12;
    hour = hour ? hour : 12; // The hour '0' should be '12'
    hour = String(hour).padStart(2, '0');

    // Construct the formatted date string
    return `${day}/${month}/${year} ${hour}:${minute}:${second} ${ampm}`;
}

async function fetchData(url) {
    try {
        const response = await fetch(url);
        const data = await response.json();
        if (!data.success) {
            showErrorMessage(data.message);
            return null;
        }
        return data.data;
    } catch (error) {
        showErrorMessage('Request failed, please try again.');
        return null;
    }
}

// Record the previous page
window.onload = function() {
    let fromMenu = sessionStorage.getItem("fromMenu");
    let historyStack = []
    if(fromMenu == "true"){
        sessionStorage.removeItem("fromMenu");
    }else{
        historyStack = JSON.parse(sessionStorage.getItem('historyStack')) || []
    }

    // Get current page path
    let currentPath = window.location.href;

    // Save current page path to history stack
    if (historyStack.length === 0) {
        historyStack.push(currentPath);
    } else {
        // Refresh current page path
        if (historyStack[historyStack.length - 1] !== currentPath) {
            historyStack.push(currentPath);
        }
        // Return to previous page by interface or back of browser
        if(historyStack.length > 1) {
            if(historyStack[historyStack.length - 2] === currentPath) {
                historyStack.splice(-1);
            }
        }
    }

    sessionStorage.setItem('historyStack', JSON.stringify(historyStack));
    console.log("historyStack: " + historyStack);
}

// Go back to the previous page
function goBack() {
    let historyStack = JSON.parse(sessionStorage.getItem('historyStack')) || [];

    if (historyStack.length > 0) {
        // Remove current page from history stack
        historyStack.splice(-1);
        
        // Save history stack to sessionStorage
        sessionStorage.setItem('historyStack', JSON.stringify(historyStack));
    }
    
    // Redirect to the last page in the history stack
    if (historyStack.length > 0) {
        window.location.href = historyStack[historyStack.length - 1];
    } else {
        // If no history, redirect to home page
        console.log('No back history');
        window.location.href = "/";
    }
}


// Animate number
function animateNumber(element, target, duration = 1000, options = {}) {
    // default options
    const {
      start = parseFloat(element.textContent) || 0,
      easing = (t) => t,
      onComplete = null,  // callback function
      decimalPlaces = 0,  // decimal places
    } = options;
  
    let startTime = null;
  
    function animation(currentTime) {
      if (!startTime) startTime = currentTime;
      const progress = Math.min((currentTime - startTime) / duration, 1);
      const easedProgress = easing(progress);
      const value = (target - start) * easedProgress + start;
      element.textContent = value.toFixed(decimalPlaces);
  
      if (progress < 1) {
        requestAnimationFrame(animation);
      } else {
        element.textContent = target.toFixed(decimalPlaces); // ensure the animation ends at the exact target value
        if (typeof onComplete === 'function') onComplete(); // execute the callback function after animation
      }
    }
  
    requestAnimationFrame(animation);
  }