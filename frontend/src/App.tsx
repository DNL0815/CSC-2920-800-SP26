import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Navbar from "./components/navbar/navbar";
import HomePage from "./components/homePage/homePage";
import SearchBar from "./components/searchBar/searchBar";
import DisplayPage from "./components/displayPage/displayPage"; // Ensure this is imported

function App() {
  // This function is still required by your SearchBar props
  const handleSearch = (searchTerm: string, filters: any) => {
    console.log("Searching for:", searchTerm, filters);
  };

  return (
    <BrowserRouter>
      <Navbar />
      {/* SearchBar stays visible on all pages */}
      <SearchBar onSearch={handleSearch} />
      
      <Routes>
        {/* The default view (Home) */}
        <Route path="/" element={<HomePage />} />
        
        {/* The search results view */}
        <Route path="/display" element={<DisplayPage />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;