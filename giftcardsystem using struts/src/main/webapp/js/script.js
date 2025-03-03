let jwt_decode;
(async () => { // dynamic import
	const module = await import("https://cdn.jsdelivr.net/npm/jwt-decode@3.1.2/build/jwt-decode.esm.js");
	jwt_decode = module.default;
})();

async function logout() {
	sessionStorage.removeItem("token");
	sessionStorage.removeItem("decodedToken");

	try {
		const response = await fetch("http://localhost:8080/GiftCardSystem/logout", {
			method: "get",
			headers: { "Content-Type": "application/json" },
			credentials: "include"
		});

	}
	catch (error) {
		alert 
		console.log("error: ", error);
	}


	window.location.href = "Login";
};



async function isTokenPresent() {

	const token = sessionStorage.getItem("token");
	if (!token) {
		console.log("No token found in sessionStorage.");
		return false;
	}

	try {

		const decodedToken = JSON.parse(sessionStorage.getItem("decodedToken"));
		console.log("Decoded Token:", decodedToken);

		if (decodedToken.exp) {
			const currentTime = Date.now() / 1000;
			if (decodedToken.exp < currentTime) {

				const response = await fetch("http://localhost:8080/GiftCardSystem/refreshAccessToken", {
					method: "get",
					headers: { "Content-Type": "application/json" },
					credentials: "include"
				});

				if (!response.ok) {
					console.error("Failed to refresh token, HTTP Status:", response.status);
					return false;
				}

				const data = await response.json();
				if (data.accessToken) {



					sessionStorage.setItem("token", data.accessToken);
					let decodetoken = jwt_decode(data.accessToken);

					sessionStorage.setItem("decodedToken", JSON.stringify(decodetoken));
					console.log("Token refreshed successfully.");

				}
				else
					return false;
			}
		}

		if (decodedToken.userType != "admin") {
			return false;
		}


		return true;
	} catch (error) {
		console.error("Error decoding token:", error);
		return false;
	}
}

async function validateToken() {

	const tokenValid = await isTokenPresent();
	if (!tokenValid) {
		window.location.href = "Login";
	}
}
