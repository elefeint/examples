const path = require('path');
const { ConsoleLogger, AsyncDuckDB, LogLevel } = require('@duckdb/duckdb-wasm');
const Worker = require('web-worker');
globalThis.Worker = Worker;

console.log("hello")

async function main() {

    const DUCKDB_DIST = path.dirname(require.resolve('@duckdb/duckdb-wasm'));
    const DUCKDB_WORKER = path.resolve(DUCKDB_DIST, './duckdb-node-eh.worker.cjs');
    const DUCKDB_WASM = path.resolve(DUCKDB_DIST, './duckdb-eh.wasm');

    const logger = new ConsoleLogger(LogLevel.WARNING);
    const worker = new Worker(DUCKDB_WORKER);
    const db = new AsyncDuckDB(logger, worker);
    console.log("***** created *****");
    await db.instantiate(DUCKDB_WASM, null);
    console.log("***** instantiated *****");
    await db.open({ allowUnsignedExtensions: true });
    console.log("*** after opening\n");

    const conn = await db.connect();
    const result = await conn.query("SELECT 42");
    console.log("*** result: ", result.toArray());
}

main().catch(console.error);

export {}
