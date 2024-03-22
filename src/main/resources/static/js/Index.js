document.addEventListener('DOMContentLoaded', function() {

  var orderPlaced = document.getElementById('orderPlaced')?.value;

    if (orderPlaced === 'true'){
        swal({
            title: "Success!",
            text: "Order placed successfully!",
            icon: "success",
            button: "close!",
        });
    }
});

function incrementQuantity(button) {
    var input = button.closest('.card-body').querySelector('input');
    var value = parseInt(input.value, 10);
    input.value = value + 1;
}

function decrementQuantity(button) {
    var input = button.closest('.card-body').querySelector('input');
    var value = parseInt(input.value, 10);
    if (value > 1) {
        input.value = value - 1;
    }
}

function addToCart(button) {
    var quantityInput = button.closest('.card-body').querySelector('input');
    var quantity = parseInt(quantityInput.value, 10);
    var productId = parseInt(button.id, 10);
    // Make an AJAX request using the Fetch API
    fetch('/add-to-cart', {
        method: 'POST',  // Adjust the method as needed
        headers: {
            'Content-Type': 'application/json',
            // Add any other headers if required
        },
        body: JSON.stringify({
            productId: productId,
            quantity: quantity
        }),
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            swal({
                title: "Success!",
                text: "Product added to cart!",
                icon: "success",
                button: "close!",
            });
        })
        .catch(error => {
            console.error('Error:', error);
            swal({
                title: "Failure!",
                text: "Please try again!",
                icon: "failure",
                button: "close!",
            });
        });
}

window.addEventListener('DOMContentLoaded', (event) => {
    var paragraphs = document.querySelectorAll('.truncate-paragraph');

    paragraphs.forEach(function(paragraph) {
        var content = paragraph.textContent.trim();
        var maxLength = 130;

        if (content.length > maxLength) {
            var truncatedContent = content.substring(0, maxLength) + '...';
            paragraph.textContent = truncatedContent;
        }
    });
});

function showPreview(event){
    if(event.target.files.length > 0){
        var src = URL.createObjectURL(event.target.files[0]);
        var preview = document.getElementById("file-ip-1-preview");
        preview.src = src;
        preview.style.display = "";
    }
}

function toggleCreditCardInfo(obj) {
    const creditCardInfo = document.getElementById('credit-card-info');
    
    if (obj.value === 'Credit Card') {
        creditCardInfo.classList.remove('hidden');
        setCreditCardFieldsRequired(true);
    } else {
        creditCardInfo.classList.add('hidden');
        setCreditCardFieldsRequired(false);
    }
}

function setCreditCardFieldsRequired(required) {
    const creditCardFields = document.querySelectorAll('#credit-card-info input');
    
    creditCardFields.forEach(field => {
        field.required = required;
    });
}

function cancelOrder(orderId) {
    fetch('/delete-order', {
        method: 'POST',  // Adjust the method as needed
        headers: {
            'Content-Type': 'application/json',
            // Add any other headers if required
        },
        body: JSON.stringify({
            orderId: orderId
        }),
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            swal({
                title: "Success!",
                text: "Order Cancel Successfully!",
                icon: "success",
                button: "close!",
            }).then(() => {
              window.location.reload();
            });
        })
        .catch(error => {
            console.error('Error:', error);
            swal({
                title: "Failure!",
                text: "Please try again!",
                icon: "failure",
                button: "close!",
            });
        });
}
