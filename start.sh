#!/usr/bin/env bash
# ==============================================================================
# TRAFFICIQ — macOS & Linux System Launcher
# ==============================================================================

clear
echo "================================================================"
echo "            TRAFFICIQ - FULL-STACK SYSTEM LAUNCHER             "
echo "   Real-Time Traffic Monitoring & Intelligent Route Database    "
echo "================================================================"
echo ""
echo "Select an option to run:"
echo ""
echo " [1] Open Frontend Website in Default Web Browser (Zero Setup)"
echo " [2] Start Local Web Server (Node.js - Port 5500)"
echo " [3] Start Python Intelligence Service (FastAPI - Port 8000)"
echo " [4] Start Java Spring Boot Backend (Port 8080)"
echo " [5] Launch Full System (Browser + Local Server)"
echo " [6] Exit"
echo ""
read -p "Enter your choice [1-6]: " choice

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"

case "$choice" in
  1)
    echo "Opening frontend in your browser..."
    if which xdg-open > /dev/null; then
      xdg-open "$DIR/frontend/index.html"
    elif which open > /dev/null; then
      open "$DIR/frontend/index.html"
    else
      echo "Please open $DIR/frontend/index.html in your browser."
    fi
    ;;
  2)
    echo "Starting Node.js Web Server on http://localhost:5500..."
    node "$DIR/server.js" 5500
    ;;
  3)
    echo "Starting Python FastAPI Service on port 8000..."
    cd "$DIR/python-service"
    if [ ! -d "venv" ]; then
      python3 -m venv venv
      source venv/bin/activate
      pip install -r requirements.txt
    else
      source venv/bin/activate
    fi
    uvicorn main:app --host 0.0.0.0 --port 8000 --reload
    ;;
  4)
    echo "Starting Java Spring Boot Backend on port 8080..."
    cd "$DIR/java-backend"
    mvn spring-boot:run
    ;;
  5)
    echo "Launching TrafficiQ Web Server and opening browser..."
    node "$DIR/server.js" 5500 &
    sleep 2
    if which xdg-open > /dev/null; then
      xdg-open "http://localhost:5500"
    elif which open > /dev/null; then
      open "http://localhost:5500"
    fi
    ;;
  *)
    exit 0
    ;;
esac
