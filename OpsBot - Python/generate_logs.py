import random as rnd
from datetime import datetime as dt, timedelta as td

# Request types
request_types = ["GET", "POST", "DELETE", "ERROR", "CRITICAL", "FAILED LOGIN"]

# API paths
paths = ["/home", "/login", "/dashboard", "/products", "/cart",
         "/checkout", "/api/data", "/profile", "/search", "/orders"]

# Error descriptions
error_data = {
    "ERROR": ["500 Internal Server Error", "Database timeout", "API failure"],
    "CRITICAL": ["Server overload", "Disk full", "Unauthorized access"]
}

# Initial timestamp
base_time = dt(2026, 4, 14, 10, 0, 0)

log_data = []

def generate_entry(index):
    current_time = base_time + td(seconds=index * 3)

    req = rnd.choices(
        request_types,
        weights=[35, 25, 15, 10, 5, 10],
        k=1
    )[0]

    user_ip = f"192.168.1.{rnd.randint(1, 255)}"
    route = rnd.choice(paths)

    if req in ["GET", "POST", "DELETE"]:
        return f"{current_time} INFO {req} {route} 200 User:{user_ip}"

    if req == "ERROR":
        return f"{current_time} ERROR {rnd.choice(error_data['ERROR'])} User:{user_ip}"

    if req == "CRITICAL":
        return f"{current_time} CRITICAL {rnd.choice(error_data['CRITICAL'])}"

    return f"{current_time} FAILED LOGIN /login 401 User:{user_ip}"


for idx in range(1500):
    entry = generate_entry(idx)
    log_data.append(entry)

# Writing logs to file
with open("server.log", "w") as file:
    file.write("\n".join(log_data))

print("Generated 1500 logs in server.log")