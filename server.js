/**
 * TRAFFICIQ — Zero-Dependency Universal Local Server
 * 
 * Works out-of-the-box on Windows, macOS, and Linux with built-in Node.js!
 * Serves frontend static files and supports live testing without complex setups.
 * 
 * Usage:
 *   node server.js [port]
 * Default port: 5500
 */

const http = require('http');
const fs = require('fs');
const path = require('path');

const PORT = process.env.PORT || parseInt(process.argv[2], 10) || 5500;
const PUBLIC_DIR = path.join(__dirname, 'frontend');

const MIME_TYPES = {
    '.html': 'text/html; charset=utf-8',
    '.css': 'text/css; charset=utf-8',
    '.js': 'application/javascript; charset=utf-8',
    '.json': 'application/json; charset=utf-8',
    '.png': 'image/png',
    '.jpg': 'image/jpeg',
    '.jpeg': 'image/jpeg',
    '.gif': 'image/gif',
    '.svg': 'image/svg+xml',
    '.ico': 'image/x-icon',
    '.csv': 'text/csv; charset=utf-8'
};

const server = http.createServer((req, res) => {
    // Enable CORS for all requests
    res.setHeader('Access-Control-Allow-Origin', '*');
    res.setHeader('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS');
    res.setHeader('Access-Control-Allow-Headers', 'Content-Type, Authorization');

    if (req.method === 'OPTIONS') {
        res.writeHead(204);
        res.end();
        return;
    }

    let reqUrl = req.url.split('?')[0];
    if (reqUrl === '/' || reqUrl === '') {
        reqUrl = '/index.html';
    }

    const filePath = path.join(PUBLIC_DIR, reqUrl);

    // Security check: ensure path is within PUBLIC_DIR
    if (!filePath.startsWith(PUBLIC_DIR)) {
        res.writeHead(403, { 'Content-Type': 'text/plain' });
        res.end('403 Forbidden');
        return;
    }

    fs.stat(filePath, (err, stats) => {
        if (err || !stats.isFile()) {
            res.writeHead(404, { 'Content-Type': 'text/html; charset=utf-8' });
            res.end(`<h2>404 Not Found</h2><p>File <code>${reqUrl}</code> was not found.</p><p><a href="/">Return to Home</a></p>`);
            return;
        }

        const ext = path.extname(filePath).toLowerCase();
        const contentType = MIME_TYPES[ext] || 'application/octet-stream';

        res.writeHead(200, { 'Content-Type': contentType });
        const stream = fs.createReadStream(filePath);
        stream.pipe(res);
    });
});

server.listen(PORT, () => {
    console.log('================================================================');
    console.log('🚀 TRAFFICIQ UNIVERSAL WEB SERVER RUNNING');
    console.log('================================================================');
    console.log(`📡 Local URL:   http://localhost:${PORT}`);
    console.log(`📂 Document Root: ${PUBLIC_DIR}`);
    console.log('💡 Press Ctrl+C to stop the server');
    console.log('================================================================');
});
