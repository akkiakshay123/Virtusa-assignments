import re as regex
import datetime as dt
import os

# Pattern to capture important events
search_pattern = r"(CRITICAL|ERROR|FAILED LOGIN)"

# Read log file
with open("server.log", "r") as file:
    log_lines = file.readlines()

# Initialize counters
event_counter = dict.fromkeys(["CRITICAL", "ERROR", "FAILED LOGIN"], 0)

# Output file name
output_file = f"security_alert_{dt.date.today()}.txt"

def process_logs(lines):
    with open(output_file, "w") as out:
        for entry in lines:
            match = regex.search(search_pattern, entry)
            if match:
                key = match.group()
                event_counter[key] += 1

                if key == "CRITICAL":
                    out.write(entry)

# Execute processing
process_logs(log_lines)

# Display results
print("Errors:", event_counter)
print("Created file size:", os.path.getsize(output_file))