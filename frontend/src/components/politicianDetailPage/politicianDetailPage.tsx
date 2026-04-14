import { useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import "./politicianDetailPage.css";

type Politician = {
  bioguideId: string;
  name: string;
  partyName: string;
  chamber: string;
  district: string;
  imageUrl: string;
  state: string;
  startYear: number;
};

type Legislation = {
  id: number;
  congress: number;
  billType: string;
  billNumber: string;
  title: string;
  introducedDate?: string;
  latestAction?: string;
};

type BillSummary = {
  id: number;
  actionDate: string;
  actionDesc: string;
  text: string;
  updateDate: string;
  versionCode: string;
};

const PoliticianDetailPage = () => {
  const { id } = useParams();
  const [politician, setPolitician] = useState<Politician | null>(null);
  const [loading, setLoading] = useState(true);
  const [bills, setBills] = useState<Legislation[]>([]);
  const [loadingBills, setLoadingBills] = useState(false);
  const [summaries, setSummaries] = useState<Map<string, BillSummary[]>>(new Map());
  const [loadingSummaries, setLoadingSummaries] = useState<string | null>(null);
  const [expandedBill, setExpandedBill] = useState<string | null>(null);
  const [syncingBill, setSyncingBill] = useState<string | null>(null);
  const [isExpanded, setIsExpanded] = useState(false);

  const fallbackImage = "https://via.placeholder.com/300x400?text=No+Photo";

  useEffect(() => {
    const fetchPoliticianAndBills = async () => {
      try {
        setLoading(true);
        
        // Step 1: Get politician from members table
        const response = await fetch(`http://localhost:8080/api/members/db`);
        if (!response.ok) {
          throw new Error(`Failed to fetch members: ${response.status}`);
        }
        
        const data = await response.json();
        const found = data.find(
          (member: Politician) => member.bioguideId === id
        );
        
        if (!found) {
          setLoading(false);
          return;
        }
        
        setPolitician(found);
        
        // Step 2: Get bills for this politician from member_legislation join
        await fetchPoliticianBills(found.bioguideId);
        
      } catch (error) {
        console.error("Error fetching politician:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchPoliticianAndBills();
  }, [id]);

  const toggleExpand = () => {
    setIsExpanded(!isExpanded);
  };

  const fetchPoliticianBills = async (bioguideId: string) => {
    setLoadingBills(true);
    try {
      // Using your MemberController endpoint
      const response = await fetch(`http://localhost:8080/api/members/${bioguideId}/legislation`);
      
      if (response.ok) {
        const billsData = await response.json();
        setBills(billsData);
      } else {
        console.error("Failed to fetch bills:", response.status);
        setBills([]);
      }
    } catch (error) {
      console.error("Error fetching bills:", error);
      setBills([]);
    } finally {
      setLoadingBills(false);
    }
  };

  const fetchBillSummaries = async (congress: number, billType: string, billNumber: string) => {
    const billKey = `${congress}-${billType}-${billNumber}`;
    
    // Toggle expansion if already loaded
    if (summaries.has(billKey)) {
      setExpandedBill(expandedBill === billKey ? null : billKey);
      return;
    }

    setLoadingSummaries(billKey);
    
    try {
      // Using your BillSummaryController endpoint
      const response = await fetch(
        `http://localhost:8080/api/bills/${congress}/${billType}/${billNumber}/summaries`
      );
      
      if (response.ok) {
        const data: BillSummary[] = await response.json();
        setSummaries(prev => new Map(prev).set(billKey, data));
        setExpandedBill(billKey);
      } else if (response.status === 404) {
        // No summaries available
        setSummaries(prev => new Map(prev).set(billKey, []));
        setExpandedBill(billKey);
      } else {
        console.error("Failed to fetch summaries:", response.status);
      }
    } catch (error) {
      console.error("Error fetching bill summaries:", error);
    } finally {
      setLoadingSummaries(null);
    }
  };

  const syncBillSummaries = async (congress: number, billType: string, billNumber: string) => {
    const billKey = `${congress}-${billType}-${billNumber}`;
    setSyncingBill(billKey);
    
    try {
      // Using your sync endpoint
      const response = await fetch(
        `http://localhost:8080/api/bills/${congress}/${billType}/${billNumber}/summaries/sync`,
        { method: 'POST' }
      );
      
      if (response.ok) {
        // Wait for sync to complete, then fetch fresh summaries
        setTimeout(async () => {
          const fetchResponse = await fetch(
            `http://localhost:8080/api/bills/${congress}/${billType}/${billNumber}/summaries`
          );
          
          if (fetchResponse.ok) {
            const data: BillSummary[] = await fetchResponse.json();
            setSummaries(prev => new Map(prev).set(billKey, data));
          } else if (fetchResponse.status === 404) {
            setSummaries(prev => new Map(prev).set(billKey, []));
          }
          setSyncingBill(null);
        }, 3000);
      } else {
        console.error("Failed to sync summaries:", response.status);
        setSyncingBill(null);
      }
    } catch (error) {
      console.error("Error syncing summaries:", error);
      setSyncingBill(null);
    }
  };

  const formatBillDisplay = (bill: Legislation) => {
    return `${bill.billType.toUpperCase()} ${bill.billNumber}`;
  };

  if (loading) {
    return (
      <div className="loading-container">
        <div className="spinner"></div>
        <h2>Loading politician details...</h2>
      </div>
    );
  }

  if (!politician) {
    return (
      <div className="not-found-container">
        <h2>Politician Not Found</h2>
        <p>We couldn't find a politician with ID: {id}</p>
        <button onClick={() => window.history.back()} className="back-button">
          Go Back
        </button>
      </div>
    );
  }

  return (
    <div className="detail-page">
      <div className="detail-card">
        <img
          src={politician.imageUrl || fallbackImage}
          alt={politician.name}
          onError={(e) => {
            (e.target as HTMLImageElement).src = fallbackImage;
          }}
        />

        <div className="detail-info">
          <h1>{politician.name}</h1>
          <p><strong>Party:</strong> {politician.partyName}</p>
          <p><strong>Chamber:</strong> {politician.chamber}</p>
          <p><strong>State:</strong> {politician.state}</p>
          <p><strong>District:</strong> {politician.district || "At-large"}</p>
          <p><strong>Term Start:</strong> {politician.startYear}</p>
          <p><strong>Bioguide ID:</strong> {politician.bioguideId}</p>
        </div>
      </div>

      {/* Bills Section */}
      <div className="bills-section">
        <div className="bills-header">
          <h2>Sponsored & Cosponsored Legislation</h2>
          {bills.length > 0 && (
            <span className="bill-count">{bills.length} bills found</span>
          )}
        </div>
        
        {loadingBills ? (
          <div className="loading-bills">
            <div className="spinner-small"></div>
            <p>Loading legislation...</p>
          </div>
        ) : bills.length === 0 ? (
          <div className="no-bills">
            <p>No legislation found for this politician.</p>
            <p className="hint">
              Note: The system syncs top-5 sponsored and cosponsored bills per member.
            </p>
            <button 
              onClick={() => fetchPoliticianBills(politician.bioguideId)}
              className="retry-button"
            >
              Refresh Bills
            </button>
          </div>
        ) : (
          <div className="bills-list">
            {bills.map((bill) => {
              const billKey = `${bill.congress}-${bill.billType}-${bill.billNumber}`;
              const billSummaries = summaries.get(billKey);
              const isExpanded = expandedBill === billKey;
              const isLoadingSummaries = loadingSummaries === billKey;
              const isSyncing = syncingBill === billKey;
              
              return (
                <div key={bill.id} className="bill-card">
                  <div 
                    className="bill-header"
                    onClick={() => fetchBillSummaries(bill.congress, bill.billType, bill.billNumber)}
                  >
                    <div className="bill-title-section">
                      <h3>{formatBillDisplay(bill)}</h3>
                      {bill.introducedDate && (
                        <span className="bill-date">
                          Introduced: {new Date(bill.introducedDate).toLocaleDateString()}
                        </span>
                      )}
                    </div>
                    <span className={`expand-icon ${isExpanded ? 'expanded' : ''}`}>
                      ▼
                    </span>
                  </div>
                  
                  <p className="bill-description">
                    {bill.title || "Title not available"}
                  </p>
                  
                  {bill.latestAction && (
                    <div className="bill-latest-action">
                      <strong>Latest Action:</strong> {bill.latestAction}
                    </div>
                  )}
                  
                  {isExpanded && (
                    <div className="bill-summaries">
                      {isLoadingSummaries ? (
                        <div className="loading-summaries">
                          <div className="spinner-small"></div>
                          <p>Loading summaries...</p>
                        </div>
                      ) : isSyncing ? (
                        <div className="syncing-summaries">
                          <div className="spinner-small"></div>
                          <p>Syncing summaries from Congress API...</p>
                        </div>
                      ) : billSummaries && billSummaries.length > 0 ? (
                        <div className="summaries-list">
                          <div className="summaries-header">
                            <h4>Bill Summaries</h4>
                            <button 
                              onClick={(e) => {
                                e.stopPropagation();
                                syncBillSummaries(bill.congress, bill.billType, bill.billNumber);
                              }}
                              className="sync-button"
                              disabled={isSyncing}
                            >
                              Sync Latest Summaries
                            </button>
                          </div>
                          {billSummaries.map((summary) => (
                            <div key={summary.id} className="summary-item">
                              <div className="summary-meta">
                                <span className="summary-date">
                                  📅 {summary.actionDate || "Date not specified"}
                                </span>
                                <span className="summary-version">
                                  Version: {summary.versionCode || "N/A"}
                                </span>
                              </div>
                              {summary.actionDesc && (
                                <p className="summary-description">
                                  <strong>Action:</strong> {summary.actionDesc}
                                </p>
                              )}
                              {summary.text && (
                                <div className="summary-text">
                                  <strong>Summary:</strong>
                                  <div dangerouslySetInnerHTML={{ __html: summary.text }} />
                                </div>
                              )}
                              {summary.updateDate && (
                                <div className="summary-update">
                                  Last Updated: {new Date(summary.updateDate).toLocaleString()}
                                </div>
                              )}
                            </div>
                          ))}
                        </div>
                      ) : (
                        <div className="no-summaries">
                          <p>No summaries available for this bill.</p>
                          <button 
                            onClick={(e) => {
                              e.stopPropagation();
                              syncBillSummaries(bill.congress, bill.billType, bill.billNumber);
                            }}
                            className="sync-button-small"
                            disabled={isSyncing}
                          >
                            Fetch Summaries from Congress API
                          </button>
                        </div>
                      )}
                    </div>
                  )}
                </div>
              );
            })}
          </div>
        )}
      </div>
    </div>
  );
};

export default PoliticianDetailPage;