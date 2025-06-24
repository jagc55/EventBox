/**
 * Formatea una fecha del formato ISO (YYYY-MM-DD) al formato español (DD/MM/YYYY)
 * @param dateString - Fecha en formato ISO (YYYY-MM-DD)
 * @returns Fecha formateada en formato DD/MM/YYYY
 */
export function formatDate(dateString: string): string {
  if (!dateString) return "";

  try {
    const date = new Date(dateString);
    return date.toLocaleDateString("es-ES", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
    });
  } catch (error) {
    console.error("Error formatting date:", error);
    return dateString;
  }
}

/**
 * Formatea una hora del formato ISO (HH:MM:SS) al formato español (HH:MM)
 * @param timeString - Hora en formato ISO (HH:MM:SS)
 * @returns Hora formateada en formato HH:MM
 */
export function formatTime(timeString: string): string {
  if (!timeString) return "";

  try {
    // Si viene en formato HH:MM:SS, extraemos solo HH:MM
    const timeParts = timeString.split(":");
    if (timeParts.length >= 2) {
      return `${timeParts[0]}:${timeParts[1]}`;
    }
    return timeString;
  } catch (error) {
    console.error("Error formatting time:", error);
    return timeString;
  }
}

/**
 * Formatea fecha y hora juntas
 * @param dateString - Fecha en formato ISO
 * @param timeString - Hora en formato ISO
 * @returns Fecha y hora formateadas como "DD/MM/YYYY a las HH:MM"
 */
export function formatDateTime(dateString: string, timeString: string): string {
  const formattedDate = formatDate(dateString);
  const formattedTime = formatTime(timeString);

  if (formattedDate && formattedTime) {
    return `${formattedDate} a las ${formattedTime}`;
  } else if (formattedDate) {
    return formattedDate;
  } else if (formattedTime) {
    return formattedTime;
  }

  return "";
}
