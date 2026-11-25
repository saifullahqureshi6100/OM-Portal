import { mkdir, copyFile } from 'node:fs/promises';
import { resolve } from 'node:path';

const root = process.cwd();
const webDir = resolve(root, 'www');
const assets = [
  'index.html',
  'app.js',
  'style.css',
  'manifest.webmanifest',
  'sw.js',
  'icon.svg'
];

await mkdir(webDir, { recursive: true });
for (const file of assets) {
  await copyFile(resolve(root, file), resolve(webDir, file));
}
console.log(`Prepared ${assets.length} web assets in ${webDir}`);
