import http.server
import socketserver
import json
import time
import threading
import urllib.request
import urllib.parse

PORT = 8081
WEBHOOK_URL = "http://localhost:8080/api/webhooks/payment"

class MockPaymentHandler(http.server.BaseHTTPRequestHandler):
    def do_POST(self):
        if self.path == '/payments/create':
            print(f"Headers: {self.headers}")
            content_length = int(self.headers.get('Content-Length', 0))
            if content_length == 0:
                print("Warning: Content-Length is 0 or missing")
                
            post_data = self.rfile.read(content_length)
            try:
                data = json.loads(post_data.decode('utf-8'))
            except json.JSONDecodeError:
                print("Error decoding JSON")
                self.send_response(400)
                self.end_headers()
                return
            
            print(f"Received payment request: {data}")
            
            # Send immediate response
            self.send_response(200)
            self.send_header('Content-type', 'application/json')
            self.end_headers()
            response = {"status": "PENDING", "paymentId": data.get("paymentId"), "orderId": data.get("orderId")}
            self.wfile.write(json.dumps(response).encode('utf-8'))
            
            # Schedule webhook callback
            threading.Timer(3.0, self.send_webhook, args=[data]).start()
            
        else:
            self.send_response(404)
            self.end_headers()

    def send_webhook(self, data):
        print("Sending webhook callback...")
        webhook_payload = {
            "orderId": data.get("orderId"),
            "paymentId": data.get("paymentId"),
            "status": "SUCCESS"
        }
        
        try:
            req = urllib.request.Request(WEBHOOK_URL)
            req.add_header('Content-Type', 'application/json')
            jsondata = json.dumps(webhook_payload)
            jsondataasbytes = jsondata.encode('utf-8')
            req.add_header('Content-Length', len(jsondataasbytes))
            
            urllib.request.urlopen(req, jsondataasbytes)
            print("Webhook sent successfully.")
        except Exception as e:
            print(f"Failed to send webhook: {e}")

print(f"Mock Payment Service running on port {PORT}")
with socketserver.TCPServer(("", PORT), MockPaymentHandler) as httpd:
    httpd.serve_forever()
