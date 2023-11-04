<?php
// Simulate a database of users (you should use a real database)
$users = [
    ['email' => 'user@example.com', 'name' => 'John Doe'],
    // Add more user data
];

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $email = $_POST["email"];

    // Check if the email exists in the simulated database
    $user = getUserByEmail($email);

    if ($user) {
        // Generate a unique reset token (in production, this should be more secure)
        $resetToken = bin2hex(random_bytes(16));

        // Store the reset token and user email in a database or a temporary store
        storeResetToken($email, $resetToken);

        // In a real application, send an email with a link to the reset_password page
        // Simulating the email sending process here
        $resetLink = "reset_password.php?token=$resetToken";
        echo "Email sent successfully with the link: $resetLink";
    } else {
        echo "Email not found. Please check your email address.";
    }
}

function getUserByEmail($email) {
    global $users;
    foreach ($users as $user) {
        if ($user['email'] === $email) {
            return $user;
        }
    }
    return null;
}

function storeResetToken($email, $token) {
    // In a real application, you would store this information securely.
    // Here, we are just simulating the storage.
    $_SESSION['reset_tokens'][$email] = $token;
}
?>
