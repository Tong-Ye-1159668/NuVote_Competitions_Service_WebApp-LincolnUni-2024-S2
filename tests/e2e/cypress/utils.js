
export function randomId(prefix) {
    const randomPart = Math.floor(10000 + Math.random() * 90000); // 5 digits
    return `${prefix}${randomPart}`;
}

export function getDateAfterDays(days) {
    const today = new Date();
    
    // get date after days
    const targetDate = new Date(today);
    targetDate.setDate(today.getDate() + days);
    
    // format date "YYYY-MM-DDTHH:mm"
    const formattedDate = targetDate.toISOString().slice(0, 16);
    
    return formattedDate;
  }