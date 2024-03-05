 function checkConnectionAndLoad() {
            var xhr = new XMLHttpRequest();
            xhr.open("GET", "index.html", true);
            xhr.onreadystatechange = function() {
                if (xhr.readyState == 4) {
                    if (xhr.status == 200) {
                        // If status is 200, load index.html
                        window.location.href = "index.html";
                    } else if (xhr.status == 404 || xhr.status == 500) {
                        // If status is 404 or 500, show error_page.html
                        window.location.href = "error_page.html";
                    }
                }
            };
            xhr.send();
        }

        // Call the function when the page loads
        window.onload = function() {
            checkConnectionAndLoad();
        };