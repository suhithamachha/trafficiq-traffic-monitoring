"""
TRAFFICIQ — Python Traffic Intelligence & Route Recommendation Microservice
FastAPI Application Entry Point.
"""
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.router import api_router

app = FastAPI(
    title="TRAFFICIQ Intelligence API",
    description="Real-time traffic telemetry analysis, graph-based shortest path routing (Dijkstra/A*), and dynamic route scoring.",
    version="1.0.0",
    docs_url="/docs",
    redoc_url="/redoc"
)

# Enable Cross-Origin Resource Sharing (CORS) for Java Backend and Web UI
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Register endpoints
app.include_router(api_router)


@app.get("/", tags=["Root"])
def root_index():
    return {
        "message": "Welcome to TRAFFICIQ Python Traffic Intelligence Service",
        "docs": "/docs",
        "health": "/health",
        "version": "1.0.0"
    }


if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)
