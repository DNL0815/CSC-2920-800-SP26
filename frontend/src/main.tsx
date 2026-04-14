import "primereact/resources/themes/lara-light-indigo/theme.css"; // theme
import "primereact/resources/primereact.min.css";                  // core styles
import "primeicons/primeicons.css";                                // icons

import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import App from "./App.tsx"

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <App />
  </StrictMode>,
)
