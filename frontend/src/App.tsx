import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Navbar from "./components/navbar/navbar";
import HomePage from "./components/homePage/homePage";
import SearchBar from "./components/searchBar/searchBar";
import { SearchFilters } from "./components/searchBar/searchBar";
import DisplayPage from "./components/displayPage/displayPage";
import PoliticianDetailPage from './components/politicianDetailPage/politicianDetailPage';

export interface Politician {
  bioguideId: string;
  name: string;
  partyName: string;
  chamber: string;
  district: string;
  imageUrl: string;
  state: string;
  startYear: number;
}

function App() {
  // Now this matches exactly what SearchBar expects
  const handleSearch = (searchTerm: string, filters: SearchFilters) => {
    console.log("Searching for:", searchTerm, filters);
  };

  return (
    <BrowserRouter>
      <Navbar />
      <SearchBar onSearch={handleSearch} />
      
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/display" element={<DisplayPage />} />
        <Route path="/politician/:id" element={<PoliticianDetailPage/>}/>
      </Routes>
    </BrowserRouter>
  );
}

export default App;