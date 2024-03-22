function sendMessage(){

    if ($("#message").val().trim() !== ""){
        let jsonOb={
            name:$("#userName").val(),
            content:$("#message").val()
        }

        $("#message-container-table").prepend(`<tr><td><b>${jsonOb.name} :</b> ${jsonOb.content}</td></tr>`)
        $("#message").val("");
    }

}

// Use this to disconnect stom
window.onbeforeunload = function(e) {
console.log("dchvjhvjhvkjhvjhvjhvkjhvhjvjh jh vjh vkjvkjhvj vjhvjkvj vb")
}
