import React, { useEffect } from "react";
import { toast, ToastContainer } from "react-toastify";

const apiUrl = 'http://localhost:8080';

export const getRecentGames = async () => {
    const uhs = localStorage.getItem("uhs");
    const XSTS_token = localStorage.getItem("XSTS_token");
    try{
        const response = await fetch(`${apiUrl}/api/xbox/recent-games`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                Authorization: `XBL3.0 x=${uhs};${XSTS_token}`,
            },
        });
        if(!response.ok){
            throw new Error(`Failed to fetch Xbox recent games. Status: ${response.status}`);
        }
        const data = await response.json();
        return data.titles || [];
    }catch(error){
        console.error(error);
        toast.error("Failed to fetch Xbox recent games.");
        throw error;
    }
};