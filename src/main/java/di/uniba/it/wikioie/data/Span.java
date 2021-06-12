/**
 * Copyright (c) 2021, the WikiOIE AUTHORS.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the University of Bari nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * GNU GENERAL PUBLIC LICENSE - Version 3, 29 June 2007
 *
 */

package di.uniba.it.wikioie.data;

import java.util.Objects;

/**
 *
 * @author pierpaolo
 */
public class Span {

    private String span;

    private int start;

    private int end;

    private float score;

    /**
     *
     * @param span
     * @param start
     * @param end
     */
    public Span(String span, int start, int end) {
        this.span = span;
        this.start = start;
        this.end = end;
        this.score = 1;
    }

    /**
     *
     * @param span
     * @param start
     * @param end
     * @param score
     */
    public Span(String span, int start, int end, float score) {
        this.span = span;
        this.start = start;
        this.end = end;
        this.score = score;
    }
    
    /**
     *
     * @return
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.span);
        hash = 89 * hash + this.start;
        hash = 89 * hash + this.end;
        return hash;
    }

    /**
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Span other = (Span) obj;
        if (this.start != other.start) {
            return false;
        }
        if (this.end != other.end) {
            return false;
        }
        if (!Objects.equals(this.span, other.span)) {
            return false;
        }
        return true;
    }

    /**
     *
     * @return
     */
    public String getSpan() {
        return span;
    }

    /**
     *
     * @param span
     */
    public void setSpan(String span) {
        this.span = span;
    }

    /**
     *
     * @return
     */
    public int getStart() {
        return start;
    }

    /**
     *
     * @param start
     */
    public void setStart(int start) {
        this.start = start;
    }

    /**
     *
     * @return
     */
    public int getEnd() {
        return end;
    }

    /**
     *
     * @param end
     */
    public void setEnd(int end) {
        this.end = end;
    }

    /**
     *
     * @return
     */
    public float getScore() {
        return score;
    }

    /**
     *
     * @param score
     */
    public void setScore(float score) {
        this.score = score;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return "Span{" + "span=" + span + ", start=" + start + ", end=" + end + '}';
    }

}
